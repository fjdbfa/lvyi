package org.example.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;




@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理所有未捕获的 Exception（全局默认异常）
     */

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity handleBusinessException(BusinessException e) {
        Result<?> result = Result.error(e.getMessage(), e.getCode());
        log.error(e.getMessage(), e);
        // 根据异常类型决定 HTTP 状态码
        HttpStatus status =e.getHttpStatus();// 400
        return new ResponseEntity(result, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleGeneralException(Exception e) {
        log.error(e.getMessage(), e);
        Result<?> result = Result.error("系统繁忙，请稍后再试", 500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body( result); // 500
    }

    /**
     * 处理 404 路径不存在异常
     */

}