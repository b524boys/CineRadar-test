package com.wztc.demo.common;

import java.lang.annotation.*;

//无需授权访问
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoTokenAccess {
}