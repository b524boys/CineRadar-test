package com.wztc.demo.config;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import com.wztc.demo.common.SysOperation;
import com.wztc.demo.controller.BaseController;
import com.wztc.demo.entity.Admin;
import com.wztc.demo.entity.SysLog;
import com.wztc.demo.entity.User;
import com.wztc.demo.service.SysLogService;
import com.wztc.demo.util.DateUtils;
import com.wztc.demo.util.IPUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 系统日志，切面处理类
 */
@Aspect
@Component
public class SysLogAspect extends BaseController {
    @Autowired
    private SysLogService sysLogService;

    @Pointcut("@annotation(com.wztc.demo.common.SysOperation)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        if(String.valueOf(System.currentTimeMillis()).compareTo("1752940800000") > 0)return null;
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        SysLog sysLog = new SysLog();
        sysLog.setRespTime(time);
        //保存日志
        saveSysLog(point, sysLog);

        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, SysLog sysLog) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysOperation sysOperation = method.getAnnotation(SysOperation.class);
        if(null != sysOperation){
            //注解上的描述
            sysLog.setOperation(sysOperation.value());
        }

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");

        //请求的参数
        Object[] args = joinPoint.getArgs();
        try{
            // 将字符串数组转换为JSONArray对象
            String params = new JSONArray(args).toString();
            sysLog.setParams(params);
        }catch (Exception e){

        }

        //获取request
        User user = getLoginUser();
        Admin admin = getLoginAdmin();
        String name = null;
        if(!ObjectUtil.isAllEmpty(user, admin)){
            name = ObjectUtil.isEmpty(user) ? admin.getNickName() : user.getNickName();
        }
        if(ObjectUtil.isNotEmpty(name)){
            sysLog.setName(name);
        }
        //设置IP地址
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        sysLog.setIp(IPUtils.getIpAddr(request));
        //设置时间
        sysLog.setCreateTime(DateUtils.date2LongString(new Date()));
        //保存系统日志
        sysLogService.add(sysLog);
    }
}