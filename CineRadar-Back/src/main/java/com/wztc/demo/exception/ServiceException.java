package com.wztc.demo.exception;

public class ServiceException extends RuntimeException{
    public ServiceException(String message){
        super(message);
    }
}
