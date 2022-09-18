package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Не пройдена валидация")
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
