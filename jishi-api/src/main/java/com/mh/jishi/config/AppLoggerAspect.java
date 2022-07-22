package com.mh.jishi.config;

import com.mh.jishi.annotation.ApiDesc;
import com.mh.jishi.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * @Author ani
 * @Description 接口 日志切面类 只关注 controller 请求层
 * @CreateTime 2021-08-19 下午 2:08
 **/
@Aspect
@Component
@Slf4j
public class AppLoggerAspect {
    //连接点 - 切入点
    @Pointcut(value = "execution(* com.mh.jishi.app..*(..)))")
    public void logPointcut() {
    }

    /**
    * 记录当前线程程序运行时间
    * 使用 Netty 的 FastThreadLocal 替代 Jdk的 ThreadLocal
    */
    FastThreadLocal<Long> startTime = new FastThreadLocal<Long>();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");

    // controller 前置通知
    @Before("logPointcut()")
    public void BeforLogger(JoinPoint joinPoint) {
        log.info("********************** app请求开始 **********************");
        //开始时间
        startTime.set(System.currentTimeMillis());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.info("请求访问时间: " + dateTimeFormatter.format(LocalDateTime.now()));
        //获取请求url
        log.info("请求url: " + request.getRequestURL());
        //获取method
        MethodSignature method = (MethodSignature)joinPoint.getSignature();
        log.info("请求方式: " + request.getMethod());
        // 获取请求方法 描述注解信息
        ApiDesc apiDesc = method.getMethod().getAnnotation(ApiDesc.class);
        if(apiDesc != null){
            String desc = apiDesc.methodDesc();
            log.info("请求接口描述: " + desc);
        }
        //获取请求参数
        log.info("请求参数列表: : " + Arrays.toString(joinPoint.getArgs()));
    }

    //controller 后置通知
    @AfterReturning(value = "logPointcut()", returning = "result")
    public void AfterReturningLogger(Object result) {
        //返回值
        try{
            ResponseUtil r =  (ResponseUtil)result;
            log.info("接口响应状态: " + r.getCode());
            log.info("接口响应信息: " + r.getMsg());
        }catch (Exception e){}

        //程序运时间(毫秒)
        log.info("请求耗时: " + ((System.currentTimeMillis() - startTime.get() )  ) + " ms");
        log.info("请求结束时间: " + dateTimeFormatter.format(LocalDateTime.now()));
        startTime.remove();
        log.info("********************** app请求结束 **********************");
    }
    //异常通知
    @AfterThrowing(value = "logPointcut()")
    public void ThrowingLogger() {
        log.error("/********************** app请求异常输出  **********************/");
        log.error("ErrorMessage：请根据异常产生时间前往异常日志查看相关信息");
    }

}
