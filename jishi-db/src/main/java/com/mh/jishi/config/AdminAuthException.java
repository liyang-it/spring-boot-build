package com.mh.jishi.config;

/**
 * @Author Lizr
 * @Description 后天管理登录异常
 * @CreateTime 2021-10-23 下午 4:12
 **/

public class AdminAuthException extends RuntimeException{
    public AdminAuthException(String msg){
        super(msg);
    }

}
