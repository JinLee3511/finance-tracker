package com.github.jinlee3511.finance_tracker.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponse( // record is immutable, equals compares the all the field values..
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(HttpStatus httpStatus, String message) {
        return new ErrorResponse(
                // getReasonPhrase: returns Short Msg of Http Status, ex) 404: "NOT FOUND"
                httpStatus.value(), httpStatus.getReasonPhrase(),
                message, LocalDateTime.now()
        );
    }
}
