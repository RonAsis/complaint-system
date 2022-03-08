package com.craft.complaintmanagementms.web.handlers;

import com.craft.complaint.common.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.util.Calendar;

@Slf4j
@RestControllerAdvice
public class GlobalErrorHandlers {

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorDto handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ErrorDto.builder()
                .errorCode(HttpStatus.NOT_FOUND)
                .message(ex.getLocalizedMessage())
                .timestamp(Calendar.getInstance().getTimeInMillis())
                .build();
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ErrorDto handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return ErrorDto.builder()
                .errorCode(HttpStatus.NOT_FOUND)
                .message(ex.getLocalizedMessage())
                .timestamp(Calendar.getInstance().getTimeInMillis())
                .build();
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDto handleGlobalError(Exception ex) {
        log.error("Global error handler exception: ", ex);
        return ErrorDto.builder()
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ex.getLocalizedMessage())
                .timestamp(Calendar.getInstance().getTimeInMillis())
                .build();
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorDto handleAccessDeniedError(AccessDeniedException ex) {
        log.error("AccessDeniedException error handler exception: ", ex);
        return ErrorDto.builder()
                .errorCode(HttpStatus.METHOD_NOT_ALLOWED)
                .message(ex.getLocalizedMessage())
                .timestamp(Calendar.getInstance().getTimeInMillis())
                .build();
    }
}
