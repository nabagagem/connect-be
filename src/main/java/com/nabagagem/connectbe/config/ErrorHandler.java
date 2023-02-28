package com.nabagagem.connectbe.config;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.servlet.ErrorHandlingControllerAdvice;
import jakarta.persistence.RollbackException;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Locale;

@AllArgsConstructor
@ControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ErrorHandler {

    private final ErrorHandlingControllerAdvice errorHandlingControllerAdvice;

    @ExceptionHandler
    public ResponseEntity<?> handleException(Throwable exception, WebRequest webRequest, Locale locale) {
        if (exception instanceof TransactionSystemException || exception instanceof RollbackException) {
            return handleException(exception.getCause(), webRequest, locale);
        }
        return errorHandlingControllerAdvice.handleException(exception, webRequest, locale);
    }

}
