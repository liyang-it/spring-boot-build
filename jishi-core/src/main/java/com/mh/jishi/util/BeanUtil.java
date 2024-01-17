package com.mh.jishi.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
/**
 * 如果是 Jar 启动 需要实现 实现 ApplicationContextAware 接口的方式获取 ApplicationContext 如果是 War
 * 启动不使用 实现 ApplicationContextAware 接口的方式获取 ApplicationContext, 改为 在启动类加载的时候手动
 */
@Component
public class BeanUtil implements ApplicationContextAware {
    protected static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static <T> T getBean(Class<T> c){
        return context.getBean(c);
    }
}
