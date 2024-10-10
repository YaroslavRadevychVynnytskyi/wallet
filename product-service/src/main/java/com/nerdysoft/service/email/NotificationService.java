package com.nerdysoft.service.email;

public interface NotificationService {
    void sendEmail(String to, String subject, String body);
}
