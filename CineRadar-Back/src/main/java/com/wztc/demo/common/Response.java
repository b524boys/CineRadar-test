package com.wztc.demo.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {

    public static final String SUCCESS = "000000";
    public static final String FAILED = "999999";

    private String code;
    private String message;
    private Object data;

    public static Response success(Object object){
        return new Response(SUCCESS, "success", object);
    }

    public static Response success(){
        return new Response(SUCCESS, "success", null);
    }


    public static Response success(String message, Object object){
        return new Response(SUCCESS, message, object);
    }

    public static Response error(String message){
        return new Response(FAILED, message, null);
    }
    public static Response error(String code, String message){
        return new Response(code, message, null);
    }

    public static Response error(ResponseCodeEnum responseCodeEnum){
        return new Response(responseCodeEnum.code, responseCodeEnum.msg, null);
    }
}