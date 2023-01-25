package com.example.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Game Not Found")
public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}