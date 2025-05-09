package com.wztc.demo.controller;

import com.wztc.demo.entity.Admin;
import com.wztc.demo.entity.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
    /**
     * 获取登录后台管理员
     * @return
     */
    public Admin getLoginAdmin(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Admin admin = (Admin)request.getSession().getAttribute("admin");
        return admin;
    }

    /**
     * 获取登录前台用户
     * @return
     */
    public User getLoginUser(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        User user = (User)request.getSession().getAttribute("user");
        return user;
    }

}
