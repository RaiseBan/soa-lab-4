package com.musicband.grammy.adapter.client;

public class SoapClientException extends RuntimeException {
    
    private final int errorCode;
    private final String errorMessage;
    private final String errorDetails;

    public SoapClientException(int errorCode, String errorMessage, String errorDetails) {
        super(errorMessage + ": " + errorDetails);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetails = errorDetails;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorDetails() {
        return errorDetails;
    }
}
