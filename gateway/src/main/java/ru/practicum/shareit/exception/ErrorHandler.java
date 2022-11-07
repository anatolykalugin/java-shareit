package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidateException(
            final ValidationException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>(Map.of("error",
                e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
            final IllegalArgumentException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>(Map.of("error",
                e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
