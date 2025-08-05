package com.child1.backend.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Object> buildResponseEntity(ErrorResponse errorResponse) {
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    /**
     * Handles MethodArgumentNotValidException. Triggered when @Valid fails.
     *
     * @param ex the MethodArgumentNotValidException that is thrown
     * @return the ErrorResponse object
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
        errorResponse.setMessage("Validation error");
        List<String> subErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            subErrors.add(fieldName + ": " + message);
        });
        errorResponse.setSubErrors(subErrors);
        return buildResponseEntity(errorResponse);
    }

    /**
     * Handles HttpMessageNotReadableException. Triggered when request JSON is malformed or missing.
     *
     * @param ex the HttpMessageNotReadableException
     * @return the ErrorResponse object
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String error = "Malformed JSON request or request body is missing";
        return buildResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, error, ex));
    }

    /**
     * Handles EntityNotFoundException. Triggered when an entity is not found in the database.
     * You would typically throw this from your service layer.
     *
     * @param ex the EntityNotFoundException
     * @return the ErrorResponse object
     */
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND);
        errorResponse.setMessage(ex.getMessage());
        return buildResponseEntity(errorResponse);
    }

    /**
     * Handles all other exceptions. A catch-all for any other exceptions.
     *
     * @param ex the Exception
     * @return the ErrorResponse object
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAllOtherExceptions(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        errorResponse.setMessage("An unexpected error occurred. Please contact support.");
        errorResponse.setDebugMessage(ex.getClass().getName() + ": " + ex.getLocalizedMessage());
        return buildResponseEntity(errorResponse);
    }
}
