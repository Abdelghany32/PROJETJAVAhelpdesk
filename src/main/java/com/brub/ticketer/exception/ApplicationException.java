package com.brub.ticketer.exception;

/**
 * Custom exception for the Ticketer application.
 * This class allows creating exceptions with a custom error code and message.
 */
public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private final String errorCode;
    private final String userMessage;

    /**
     * Creates a new application exception with an error code and message.
     * 
     * @param errorCode Internal error code
     * @param message Technical message for logs
     * @param userMessage User-friendly message
     */
    public ApplicationException(String errorCode, String message, String userMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }

    /**
     * Creates a new application exception with an error code, message and cause.
     * 
     * @param errorCode Internal error code
     * @param message Technical message for logs
     * @param userMessage User-friendly message
     * @param cause Original exception
     */
    public ApplicationException(String errorCode, String message, String userMessage, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }

    /**
     * Creates a new application exception with a message.
     * The default error code is "ERR_GENERAL".
     * 
     * @param message Technical message for logs
     * @param userMessage User-friendly message
     */
    public ApplicationException(String message, String userMessage) {
        this("ERR_GENERAL", message, userMessage);
    }

    /**
     * Creates a new application exception with a message and cause.
     * The default error code is "ERR_GENERAL".
     * 
     * @param message Technical message for logs
     * @param userMessage User-friendly message
     * @param cause Original exception
     */
    public ApplicationException(String message, String userMessage, Throwable cause) {
        this("ERR_GENERAL", message, userMessage, cause);
    }

    /**
     * Returns the error code associated with this exception.
     * 
     * @return Error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the user-friendly message.
     * 
     * @return User message
     */
    public String getUserMessage() {
        return userMessage;
    }
}