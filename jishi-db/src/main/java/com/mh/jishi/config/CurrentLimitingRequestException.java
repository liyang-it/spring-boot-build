package com.mh.jishi.config;

/**
 * <h2>接口限流异常</h2>
 * <p>
 *
 * </p>
 *
 * @author Evan <1922802352@qq.com>
 * @since 2022年06月23日 9:55
 */
public class CurrentLimitingRequestException extends RuntimeException{

    public CurrentLimitingRequestException(String msg){
        super(msg);
    }
}
