package com.nabagagem.connectbe.domain.exceptions;

import org.springframework.http.HttpStatus;

public class ReportNotFoundException extends RuntimeException implements BusinessException {
    @Override
    public ErrorType getErrorType() {
        return ErrorType.REPORT_NOT_FOUND;
    }

    @Override
    public Object[] getArgs() {
        return new Object[0];
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
