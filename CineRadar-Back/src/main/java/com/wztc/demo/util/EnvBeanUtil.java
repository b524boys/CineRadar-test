package com.wztc.demo.util;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 获取环境变量参数
 */
@Component
public class EnvBeanUtil implements EnvironmentAware {

    private static Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }

    public static String getString(String key) {
        return env.getProperty(key);
    }

}