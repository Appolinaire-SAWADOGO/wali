package com.wali.wallet_service.exceptions;

public class TransactionStatusIsNotPendingException extends RuntimeException {
    public TransactionStatusIsNotPendingException(String message) {
        super(message);
    }
}
