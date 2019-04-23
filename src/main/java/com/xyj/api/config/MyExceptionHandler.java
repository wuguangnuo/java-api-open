package com.xyj.api.config;

import com.xyj.api.model.ApiRes;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class MyExceptionHandler {
    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ApiRes myHandler(Exception ex) {
        return ApiRes.fail(ex.getMessage());
    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiRes myValidateHandler(MethodArgumentNotValidException ex) {

        List<FieldError> errors = ex.getBindingResult().getFieldErrors();

        StringBuilder sb = new StringBuilder();

        for (FieldError err : errors) {
            String errMsg = err.getDefaultMessage();
            sb.append(err.getField()).append("-").append(errMsg).append(";");
        }

        return ApiRes.fail(sb.toString());
    }
}
