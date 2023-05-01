package com.nabagagem.connectbe.domain.exceptions;

import org.springframework.http.HttpStatus;

public interface BusinessException {
    ErrorType getErrorType();

    Object[] getArgs();

    HttpStatus getHttpStatus();
}
