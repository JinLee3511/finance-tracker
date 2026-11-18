package com.github.jinlee3511.finance_tracker.exception;

public class TransactionNotFoundException extends NotFoundException {
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
