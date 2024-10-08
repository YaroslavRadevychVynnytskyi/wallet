package com.nerdysoft.service.impl;

import com.nerdysoft.dto.api.request.CommissionRequestMessage;
import com.nerdysoft.dto.feign.ConvertAmountRequestDto;
import com.nerdysoft.entity.Commission;
import com.nerdysoft.feign.CurrencyExchangeFeignClient;
import com.nerdysoft.repo.CommissionRepository;
import com.nerdysoft.service.CommissionService;
import com.nerdysoft.service.strategy.CommissionStrategy;
import com.nerdysoft.service.strategy.handler.CommissionHandler;
import com.nerdysoft.service.strategy.handler.LoanCommissionHandler;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommissionServiceImpl implements CommissionService {
    private final LoanCommissionHandler loanCommissionHandler;
    private final CurrencyExchangeFeignClient currencyExchangeFeignClient;
    private final CommissionRepository commissionRepository;
    private final CommissionStrategy commissionStrategy;

    @RabbitListener(queues = "${rabbitmq.queue.commission-queue}")
    @Override
    @SneakyThrows
    public void calculateCommission(@Payload CommissionRequestMessage message) {
        BigDecimal walletAmount = convertToUsd(message, CommissionRequestMessage::getWalletAmount);
        BigDecimal loanLimitAmount = convertToUsd(message, CommissionRequestMessage::getLoanLimitAmount);

        List<CommissionHandler> commissionHandlers = commissionStrategy.get(walletAmount, message.isLoanLimitUsed());

        BigDecimal totalCommissionAmount = commissionHandlers.stream()
                .map(ch -> calculateCommissionForHandler(ch, message, loanLimitAmount, walletAmount))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal originalCurrencyCommission = convertCommissionToOriginalCurrency(message.getFromWalletCurrency(), totalCommissionAmount);

        Commission commission = new Commission(totalCommissionAmount, originalCurrencyCommission, message);
        commissionRepository.save(commission);
    }

    private BigDecimal convertToUsd(CommissionRequestMessage requestDto,
                                    Function<CommissionRequestMessage, BigDecimal> amountResolver) {
        BigDecimal amount = amountResolver.apply(requestDto);

        if (!requestDto.getFromWalletCurrency().equals("USD")) {
            return currencyExchangeFeignClient.convert(new ConvertAmountRequestDto(
                    requestDto.getFromWalletCurrency(),
                    "USD",
                    amount)
            ).getBody().convertedAmount();
        }
        return amount;
    }

    private BigDecimal calculateCommissionForHandler(CommissionHandler handler,
                                                     CommissionRequestMessage message,
                                                     BigDecimal loanLimitAmount,
                                                     BigDecimal walletAmount) {
        return handler.equals(loanCommissionHandler)
                ? handler.getCommission(message, loanLimitAmount)
                : handler.getCommission(message, walletAmount);
    }

    private BigDecimal convertCommissionToOriginalCurrency(String originalCurrency, BigDecimal commission) {
        if (!originalCurrency.equals("USD")) {
            commission = currencyExchangeFeignClient.convert(new ConvertAmountRequestDto(
                    "USD",
                    originalCurrency,
                    commission
            )).getBody().convertedAmount();
        }

        return commission;
    }
}
