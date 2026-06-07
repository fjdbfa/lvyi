package org.example.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Data
public class BusinessException extends RuntimeException {

    private String detail;
    private HttpStatus httpStatus;
    private int code;
    public BusinessException(String message) {

        this(message, 400, HttpStatus.BAD_REQUEST);

    }

    public BusinessException(String message, int code,HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
    }



}