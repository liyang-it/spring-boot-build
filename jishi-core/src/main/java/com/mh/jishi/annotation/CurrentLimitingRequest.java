package com.mh.jishi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h2>用于限制接口请求</h2>
 * <p>
 *  标注在 接口方法上
 * </p>
 *
 * @author Evan <1922802352@qq.com>
 * @since 2022年6月22日 09:22:22
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentLimitingRequest {
    /**
     * 接口间隔多少秒之后才能访问, 默认5秒
     */
    int seconds() default 5;

}
