package com.nabagagem.connectbe.config;

import com.nabagagem.connectbe.domain.exceptions.BadRequestException;
import com.nabagagem.connectbe.domain.exceptions.BusinessException;
import com.nabagagem.connectbe.domain.exceptions.ErrorType;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.servlet.ErrorHandlingControllerAdvice;
import jakarta.persistence.RollbackException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@ControllerAdvice
@RequiredArgsConstructor
public class ErrorHandler {
    private final ErrorHandlingControllerAdvice errorHandlingControllerAdvice;
    private final MessageSource messageSource;
    private final Set<String> ukIndexes = Set.of("uc_profile_slug", "uc_profile_email", "uc_jobmedia_position");

    @SuppressWarnings("unchecked")
    @ExceptionHandler
    public ResponseEntity<?> handleException(Throwable exception, WebRequest webRequest, Locale locale) {
        if (exception instanceof TransactionSystemException || exception instanceof RollbackException) {
            return handleException(exception.getCause(), webRequest, locale);
        }
        if (exception instanceof MaxUploadSizeExceededException) {
            return renderBusinessException(BadRequestException.builder()
                    .errorType(ErrorType.INVALID_FILE_SIZE)
                    .build());
        }
        if (exception instanceof BusinessException businessException) {
            return renderBusinessException(businessException);
        }
        if (exception instanceof DataIntegrityViolationException integrityViolationException) {
            String message = integrityViolationException.getMessage();
            return ukIndexes.stream().filter(message::contains)
                    .findAny()
                    .map(this::renderUkResponse)
                    .orElseGet(() -> (ResponseEntity<Error>) renderDefaultError());
        }
        return errorHandlingControllerAdvice.handleException(exception, webRequest, locale);
    }

    private ResponseEntity<?> renderDefaultError() {
        return renderUkResponse("general_uk");
    }

    private ResponseEntity<Error> renderUkResponse(String message) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new Error(
                        ErrorType.UNIQUE_VALUE,
                        messageSource.getMessage(message.concat("_title"),
                                null, LocaleContextHolder.getLocale()),
                        messageSource.getMessage(message.concat("_description"),
                                null, LocaleContextHolder.getLocale())
                )
        );
    }

    private ResponseEntity<?> renderBusinessException(BusinessException businessException) {
        Optional<String> key = Optional.ofNullable(businessException.getErrorType())
                .map(Objects::toString);
        Locale locale = LocaleContextHolder.getLocale();
        return ResponseEntity.status(businessException.getHttpStatus())
                .body(new Error(
                        businessException.getErrorType(),
                        key.map(error -> messageSource.getMessage(
                                        error.concat("_TITLE"), null, locale))
                                .orElse(null),
                        key.map(error -> messageSource.getMessage(
                                        error.concat("_DESCRIPTION"),
                                        businessException.getArgs(), locale))
                                .orElse(null)
                ));
    }

    public record Error(
            ErrorType errorType,
            String title,
            String description
    ) {
    }
}
