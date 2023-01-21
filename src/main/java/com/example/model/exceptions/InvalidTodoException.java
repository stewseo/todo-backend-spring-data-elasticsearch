package com.example.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Game Not Found")
public class InvalidTodoException extends RuntimeException   {
    public InvalidTodoException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}