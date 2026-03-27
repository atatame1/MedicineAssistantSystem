package com.atatame.medicineassistantsystem.exception;

import com.atatame.medicineassistantsystem.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result BusinessExceptionHandler(BusinessException e){
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result RuntimeExceptionHandler(RuntimeException e){
        log.error("RuntimeException", e);
        return Result.fail("系统错误");
    }
}
