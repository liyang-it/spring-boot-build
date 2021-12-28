package com.mh.jishi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author ani
 * @Description 接口描述
 * @CreateTime 2021-08-20 下午 6:01
 **/
@Target({ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiDesc {
    String methodDesc() default "未知描述";
}
