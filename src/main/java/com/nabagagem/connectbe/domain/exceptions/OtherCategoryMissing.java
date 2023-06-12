package com.nabagagem.connectbe.domain.exceptions;

import org.springframework.http.HttpStatus;

public class OtherCategoryMissing extends RuntimeException implements BusinessException {
    @Override
    public ErrorType getErrorType() {
        return ErrorType.MISSING_OTHER_CATEGORY;
    }

    @Override
    public Object[] getArgs() {
        return null;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
