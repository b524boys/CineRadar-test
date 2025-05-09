package com.wztc.demo.common;

public enum RoleEnum {

    ADMIN("ADMIN", "管理员"),
    SUB_ADMIN("SUB_ADMIN", "子管理员"),
    USER("USER", "普通用户"),
    ;

    public String code;
    public String msg;

    RoleEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}