package com.nerdysoft.service.loanlimit.impl;

import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.repo.loanlimit.LoanLimitRepository;
import com.nerdysoft.service.email.NotificationService;
import com.nerdysoft.service.loanlimit.LoanLimitNotificationService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanLimitNotificationServiceImpl implements LoanLimitNotificationService {
    private final LoanLimitRepository loanLimitRepository;
    private final NotificationService notificationService;

    @Value("${mail.subject.loan-limit}")
    private String subject;

    @Value("${mail.body.loan-limit}")
    private String body;

    @Scheduled(cron = "0 0 8 * * ?")
    @Override
    public void sendOverdueLoanLimitNotification() {
        List<LoanLimit> overdueLoans = loanLimitRepository.findOverdueLoans(LocalDateTime.now());

        overdueLoans.stream()
                .map(LoanLimit::getAccountEmail)
                .forEach(e -> notificationService.sendEmail(e, subject, body));
    }
}
