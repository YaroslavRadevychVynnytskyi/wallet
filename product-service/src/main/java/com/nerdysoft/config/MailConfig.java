package com.nerdysoft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class MailConfig {
    @Bean
    JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl();
    }
}
