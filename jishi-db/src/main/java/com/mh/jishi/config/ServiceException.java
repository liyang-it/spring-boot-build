package com.mh.jishi.config;

/**
 * @Author ani
 * @Description 服务层异常
 * @CreateTime 2021/8/11 17:09
 **/
public class ServiceException extends RuntimeException{
    public ServiceException(String errorMsg){
        super(errorMsg);
    }
}
