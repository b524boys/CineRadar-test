package com.wztc.demo.config;

import cn.hutool.core.util.ObjectUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.wztc.demo.common.Constants;
import com.wztc.demo.common.NoTokenAccess;
import com.wztc.demo.common.ResponseCodeEnum;
import com.wztc.demo.common.RoleEnum;
import com.wztc.demo.entity.Admin;
import com.wztc.demo.entity.User;
import com.wztc.demo.exception.CustomException;
import com.wztc.demo.service.AdminService;
import com.wztc.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private AdminService adminService;


    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //controller方法上带有注解:NoTokenAccess, 则直接放行
        boolean bLetGo = false;
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            NoTokenAccess methodAnnotation = handlerMethod.getMethodAnnotation(NoTokenAccess.class);
            if (null != methodAnnotation){
                bLetGo = true;
            }
        }else {
            // 如果不是映射到方法直接通过
            bLetGo = true;
        }

        //header中获取token
        String token = request.getHeader(Constants.TOKEN);
        if(ObjectUtil.isEmpty(token)){
            //参数中获取token
            token = request.getParameter(Constants.TOKEN);
        }

        //token为空 && 没被放行
        if(ObjectUtil.isEmpty(token) && !bLetGo){
            System.out.println("token为空且没被放行");
            throw new CustomException(ResponseCodeEnum.TOKEN_INVALID);
        }

        if(ObjectUtil.isNotEmpty(token)){
            Admin admin = null;
            User user = null;
            String strPassword = "";
            try{
                //解析token数据
                String tokenInfo = JWT.decode(token).getAudience().get(0);
                int id = Integer.parseInt(tokenInfo.split("-")[0]);
                String role = tokenInfo.split("-")[1];


                //根据角色判断用户
                if(RoleEnum.ADMIN.code.equals(role) || RoleEnum.SUB_ADMIN.code.equals(role)){
                    admin = adminService.selectById(id);
                    if(ObjectUtil.isNotEmpty(admin)){
                        strPassword = admin.getPassword();
                        request.getSession().setAttribute("admin", admin);
                    }
                }else if(RoleEnum.USER.code.equals(role)){
                    user = userService.selectById(id);
                    if(ObjectUtil.isNotEmpty(user)){
                        strPassword = user.getPassword();
                        request.getSession().setAttribute("user", user);
                    }
                }
            }catch (Exception e){
                throw new CustomException(ResponseCodeEnum.TOKEN_INVALID);
            }

            if(ObjectUtil.isEmpty(admin) && ObjectUtil.isEmpty(user)){
                throw new CustomException(ResponseCodeEnum.USER_NOT_EXIST);
            }

            try{
                //根据用户密码校验token
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(strPassword)).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(token);

                //token过期时间 < 当前时间    抛出异常
                long currentTime = System.currentTimeMillis() / 1000;
                if (decodedJWT.getExpiresAt().getTime() / 1000 < currentTime) {
                    System.out.println("Token has expired");
                    throw new JWTVerificationException("Token has expired");
                }

            }catch (JWTVerificationException e){
                throw new CustomException(ResponseCodeEnum.TOKEN_INVALID);
            }
        }

        return true;
    }
}
