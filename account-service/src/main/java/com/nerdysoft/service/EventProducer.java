package com.nerdysoft.service;

import com.nerdysoft.dto.rabbit.TransactionEvent;

public interface EventProducer {
    void sendTransactionEvent(TransactionEvent event);
}
