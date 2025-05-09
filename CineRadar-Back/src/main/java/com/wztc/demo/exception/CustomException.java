package com.wztc.demo.exception;


import com.wztc.demo.common.ResponseCodeEnum;

public class CustomException extends RuntimeException {
    private String code;
    private String msg;

    public CustomException(ResponseCodeEnum responseCodeEnum) {
        this.code = responseCodeEnum.code;
        this.msg = responseCodeEnum.msg;
    }

    public CustomException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}