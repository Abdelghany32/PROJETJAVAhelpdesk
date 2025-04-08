package com.brub.ticketer.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Get the error code
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        // By default, we use a generic error template
        String viewName = "error";

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            // Add standard attributes for the error page
            model.addAttribute("status", statusCode);
            model.addAttribute("error", getErrorMessage(statusCode));
            model.addAttribute("message", getErrorDescription(statusCode));

            // For common errors, we use specific templates
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                viewName = "error/404";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                viewName = "error/403";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                viewName = "error/500";
            }

            // Add trace attribute for 500 errors in development mode
            if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
                if (throwable != null) {
                    model.addAttribute("trace", getStackTraceAsString(throwable));
                }
            }
        }

        return viewName;
    }

    // Add the getErrorPath method to fix the compilation error
    @Override
    public String getErrorPath() {
        return "/error";
    }

    private String getErrorMessage(int statusCode) {
        switch (statusCode) {
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized";
            case 403:
                return "Access Denied";
            case 404:
                return "Page Not Found";
            case 405:
                return "Method Not Allowed";
            case 500:
                return "Internal Server Error";
            case 501:
                return "Not Implemented";
            case 502:
                return "Bad Gateway";
            case 503:
                return "Service Unavailable";
            default:
                return "Unexpected Error";
        }
    }

    private String getErrorDescription(int statusCode) {
        switch (statusCode) {
            case 400:
                return "The request contains invalid or malformed parameters.";
            case 401:
                return "Authentication is required to access this resource.";
            case 403:
                return "You don't have permission to access this page or resource.";
            case 404:
                return "The page or resource you are trying to access does not exist or has been moved.";
            case 405:
                return "The HTTP method used is not allowed for this resource.";
            case 500:
                return "An internal server error occurred while processing your request.";
            case 501:
                return "The requested functionality is not implemented on this server.";
            case 502:
                return "The server, while acting as a gateway or proxy, received an invalid response from the upstream server.";
            case 503:
                return "The service is temporarily unavailable.";
            default:
                return "An unexpected error occurred while processing your request.";
        }
    }

    private String getStackTraceAsString(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");

        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }

        if (throwable.getCause() != null) {
            sb.append("Caused by: ").append(getStackTraceAsString(throwable.getCause()));
        }

        return sb.toString();
    }
}