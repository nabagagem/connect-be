package com.nabagagem.connectbe.config;

import com.nabagagem.connectbe.domain.exceptions.BadRequestException;
import com.nabagagem.connectbe.domain.exceptions.BusinessException;
import com.nabagagem.connectbe.domain.exceptions.ErrorType;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.servlet.ErrorHandlingControllerAdvice;
import jakarta.persistence.RollbackException;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Locale;

@ControllerAdvice
@AllArgsConstructor
public class ErrorHandler {
    private final ErrorHandlingControllerAdvice errorHandlingControllerAdvice;
    private final MessageSource messageSource;

    @ExceptionHandler
    public ResponseEntity<?> handleException(Throwable exception, WebRequest webRequest, Locale locale) {
        if (exception instanceof TransactionSystemException || exception instanceof RollbackException) {
            return handleException(exception.getCause(), webRequest, locale);
        }
        if (exception instanceof MaxUploadSizeExceededException) {
            return renderBusinessException(BadRequestException.builder()
                    .errorType(ErrorType.INVALID_PROFILE_PIC_SIZE)
                    .build());
        }
        if (exception instanceof BusinessException businessException) {
            return renderBusinessException(businessException);
        }
        return errorHandlingControllerAdvice.handleException(exception, webRequest, locale);
    }

    private ResponseEntity<?> renderBusinessException(BusinessException businessException) {
        String key = businessException.getErrorType().toString();
        Locale locale = LocaleContextHolder.getLocale();
        return ResponseEntity.status(businessException.getHttpStatus())
                .body(new Error(
                        businessException.getErrorType(),
                        messageSource.getMessage(
                                key.concat("_TITLE"), null, locale),
                        messageSource.getMessage(key
                                .concat("_DESCRIPTION"), businessException.getArgs(), locale)
                ));
    }

    public record Error(
            ErrorType errorType,
            String title,
            String description
    ) {
    }
}
