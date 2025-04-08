package com.brub.ticketer.config;

import com.brub.ticketer.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle access denied exceptions (403)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(Exception ex, Model model) {
        logger.error("Access denied: {}", ex.getMessage());
        
        model.addAttribute("status", HttpStatus.FORBIDDEN.value());
        model.addAttribute("error", "Access Denied");
        model.addAttribute("message", "You don't have permission to access this resource.");
        
        return "error/403";
    }

    // Handle entity not found exceptions (404)
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(Exception ex, Model model) {
        logger.error("Entity not found: {}", ex.getMessage());
        
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", "Resource Not Found");
        model.addAttribute("message", "The requested resource does not exist or has been removed.");
        
        return "error/404";
    }

    // Handle application exceptions
    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleApplicationException(ApplicationException ex, Model model, HttpServletRequest request) {
        logger.error("Application error [{}]: {}", ex.getErrorCode(), ex.getMessage(), ex);
        
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", "Application Error");
        model.addAttribute("message", ex.getUserMessage());
        model.addAttribute("errorCode", ex.getErrorCode());
        
        // Add trace details only in development mode
        if (isDevelopmentMode(request)) {
            model.addAttribute("trace", getStackTraceAsString(ex));
        }
        
        return "error/500";
    }

    // Handle data access exceptions (500)
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDataAccessException(Exception ex, Model model, HttpServletRequest request) {
        logger.error("Database error: {}", ex.getMessage(), ex);
        
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", "Database Error");
        model.addAttribute("message", "An error occurred while accessing the database. Our technical team has been notified.");
        
        // Add trace details only in development mode
        if (isDevelopmentMode(request)) {
            model.addAttribute("trace", getStackTraceAsString(ex));
        }
        
        return "error/500";
    }

    // Handle all other exceptions (500)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model, HttpServletRequest request) {
        logger.error("Unhandled error: {}", ex.getMessage(), ex);
        
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", "Internal Server Error");
        model.addAttribute("message", "An unexpected error has occurred. Our technical team has been notified.");
        
        // Add trace details only in development mode
        if (isDevelopmentMode(request)) {
            model.addAttribute("trace", getStackTraceAsString(ex));
        }
        
        return "error/500";
    }

    // Utility method to convert error trace to string
    private String getStackTraceAsString(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
    
    // Utility method to determine if we are in development mode
    private boolean isDevelopmentMode(HttpServletRequest request) {
        // You can implement more sophisticated logic here
        // For example, check a Spring property like spring.profiles.active
        // For now, we use a simple approach based on the request host
        String host = request.getServerName();
        return host.equals("localhost") || host.equals("127.0.0.1");
    }
}