package com.wztc.demo.common;

public enum ResponseCodeEnum {
    SUCCESS("000000", "成功"),

    PARAM_ERROR("400000", "参数异常"),
    TOKEN_INVALID("401000", "token失效"),
    USER_NOT_EXIST("401000", "用户不存在"),
    PARAM_MISSED_ERROR("400100", "参数缺失"),
    SYSTEM_ERROR("500000", "系统异常"),
    USER_ATTACKED("401000", "用户数据被篡改"),
    ;

    public String code;
    public String msg;

    ResponseCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}