package com.nabagagem.connectbe.domain.exceptions;

import org.springframework.http.HttpStatus;

public class ThreadBlockedByAnotherUser extends RuntimeException implements BusinessException {
    @Override
    public ErrorType getErrorType() {
        return ErrorType.THREAD_BLOCKED_BY_ANOTHER_USER;
    }

    @Override
    public Object[] getArgs() {
        return new Object[0];
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
