package com.wztc.demo.exception;

import com.wztc.demo.common.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response handleGlobalException(Exception e){
        e.printStackTrace();
        return Response.error(e.getMessage());
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Response handleServiceException(ServiceException e){
        e.printStackTrace();
        return Response.error(e.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Response handleCustomException(CustomException e){
        e.printStackTrace();
        return Response.error(e.getCode(), e.getMsg());
    }
}
