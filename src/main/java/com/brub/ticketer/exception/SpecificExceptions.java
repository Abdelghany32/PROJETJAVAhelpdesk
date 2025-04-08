package com.brub.ticketer.exception;

import javax.persistence.EntityNotFoundException;

/**
 * Class that groups specific exceptions for the Ticketer application.
 */
public class SpecificExceptions {

    /**
     * Exception thrown when a ticket is not found.
     */
    public static class TicketNotFoundException extends EntityNotFoundException {
        private static final long serialVersionUID = 1L;
        
        public TicketNotFoundException(Long id) {
            super("Ticket not found with ID: " + id);
        }
    }
    
    /**
     * Exception thrown when a user is not found.
     */
    public static class UserNotFoundException extends EntityNotFoundException {
        private static final long serialVersionUID = 1L;
        
        public UserNotFoundException(Long id) {
            super("User not found with ID: " + id);
        }
        
        public UserNotFoundException(String email) {
            super("User not found with email: " + email);
        }
    }
    
    /**
     * Exception thrown when a student is not found.
     */
    public static class StudentNotFoundException extends EntityNotFoundException {
        private static final long serialVersionUID = 1L;
        
        public StudentNotFoundException(Long id) {
            super("Student not found with ID: " + id);
        }
        
        public StudentNotFoundException(String registration) {
            super("Student not found with registration: " + registration);
        }
    }
    
    /**
     * Exception thrown when an agent is not found.
     */
    public static class AgentNotFoundException extends EntityNotFoundException {
        private static final long serialVersionUID = 1L;
        
        public AgentNotFoundException(Long id) {
            super("Agent not found with ID: " + id);
        }
    }
    
    /**
     * Exception thrown when unauthorized operations are attempted.
     */
    public static class UnauthorizedOperationException extends ApplicationException {
        private static final long serialVersionUID = 1L;
        
        public UnauthorizedOperationException(String message) {
            super("ERR_UNAUTHORIZED", message, "You do not have permission to perform this operation.");
        }
    }
    
    /**
     * Exception thrown when there are validation errors.
     */
    public static class ValidationException extends ApplicationException {
        private static final long serialVersionUID = 1L;
        
        public ValidationException(String message) {
            super("ERR_VALIDATION", message, "There are validation errors in your request.");
        }
        
        public ValidationException(String field, String message) {
            super("ERR_VALIDATION", "Validation error on field: " + field + ". " + message, 
                  "There is a validation error in the " + field + " field: " + message);
        }
    }
    
    /**
     * Exception thrown when there is a data conflict.
     */
    public static class DataConflictException extends ApplicationException {
        private static final long serialVersionUID = 1L;
        
        public DataConflictException(String message) {
            super("ERR_DATA_CONFLICT", message, "A conflict was detected in the data.");
        }
    }
    
    /**
     * Exception thrown when a service is unavailable.
     */
    public static class ServiceUnavailableException extends ApplicationException {
        private static final long serialVersionUID = 1L;
        
        public ServiceUnavailableException(String serviceName) {
            super("ERR_SERVICE_UNAVAILABLE", "Service unavailable: " + serviceName, 
                  "The service is temporarily unavailable. Please try again later.");
        }
    }
}