package com.ssafy.ssapay.global.error;

import com.ssafy.ssapay.global.error.response.ErrorResponse;
import com.ssafy.ssapay.global.error.type.BadRequestException;
import com.ssafy.ssapay.global.error.type.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"com.ssafy.ssapay"})
@Slf4j
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(new ErrorResponse(message), status);
    }

    @ExceptionHandler(value = {BadRequestException.class, DataNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        log.error("Error: ", e);
        return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Error: ", e);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다.");
    }
}
