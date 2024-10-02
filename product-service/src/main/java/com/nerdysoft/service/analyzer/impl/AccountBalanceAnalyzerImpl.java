package com.nerdysoft.service.analyzer.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nerdysoft.dto.feign.Transaction;
import com.nerdysoft.dto.feign.UserActivityEvent;
import com.nerdysoft.dto.feign.enums.EntityType;
import com.nerdysoft.feign.AuditFeignClient;
import com.nerdysoft.service.analyzer.AccountBalanceAnalyzer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountBalanceAnalyzerImpl implements AccountBalanceAnalyzer {
    private final AuditFeignClient auditFeignClient;
    private final ObjectMapper objectMapper;

    @Override
    public boolean isNewAccount(UUID accountId) {
        List<UserActivityEvent> activityLogs = auditFeignClient.getUserActivityLogsByUserId(accountId).getBody();

        return activityLogs.stream()
                .noneMatch(log -> log.entityType().equals(EntityType.TRANSACTION));
    }

    @Override
    public boolean hasBalanceAboveForLastMonth(UUID accountId, BigDecimal threshold) {
        List<UserActivityEvent> activityLogs = auditFeignClient.getUserActivityLogsByUserId(accountId).getBody();



        BigDecimal maxBalanceForLastMonth = activityLogs.stream()
                .filter(log -> log.entityType().equals(EntityType.TRANSACTION)
                        && log.timestamp().isAfter(LocalDateTime.now().minusMonths(1)))
                .map(log -> {
                    Transaction transaction;
                    try {
                        transaction = objectMapper.readValue(log.newData(), Transaction.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Can't process Json", e);
                    }
                    System.out.println(transaction.walletBalance());
                    return transaction.walletBalance();
                })
                .max(BigDecimal::compareTo)
                .orElseThrow();

        System.out.println("Max balance for last month" + maxBalanceForLastMonth.longValue());

        return maxBalanceForLastMonth.compareTo(threshold) >= 0;
    }

    @Override
    public boolean hasTurnoverAboveForLastMonth(UUID accountId, BigDecimal threshold) {
        List<UserActivityEvent> activityLogs = auditFeignClient.getUserActivityLogsByUserId(accountId).getBody();

        //activityLogs.stream().map(UserActivityEvent::newData).forEach(System.out::println);

        BigDecimal turnover = activityLogs.stream()
                .filter(log -> log.entityType().equals(EntityType.TRANSACTION)
                        && log.timestamp().isAfter(LocalDateTime.now().minusMonths(1)))
                .map(log -> {
                            Transaction transaction;
                            try {
                                transaction = objectMapper.readValue(log.newData(), Transaction.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException("Can't process Json", e);
                            }
                            return transaction.amount();
                        }
                ).reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("Turnover: " + turnover.intValue());

        return turnover.compareTo(threshold) >= 0;
    }
}
