package com.mh.jishi.config;

import com.mh.jishi.exception.ConcurrentUpdateException;
import com.mh.jishi.util.ResponseUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Set;

/**
 * 全局异常处理
 */
@RestControllerAdvice
@Order
public class GlobalExceptionHandler {

    private Log logger = LogFactory.getLog(GlobalExceptionHandler.class);
    private Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    public String getRequestUrl(HttpServletRequest request){
        return request.getRequestURL().toString();
    }
    @ExceptionHandler(ConcurrentUpdateException.class)
    @ResponseBody
    public Object ServiceException(ConcurrentUpdateException e, HttpServletRequest request) {
        /**
         * 自定义 运行时异常
         */
        log.error("系统异常, 异常原因: [更新数据失败，数据已被其他线程修改], 接口地址: [{}]", getRequestUrl(request));
        return ResponseUtil.updateFailed();
    }
    /** 自定义异常 处理  NoHandlerFoundException  404 异常
     * @return 根据自定义返回结果 返回对应内容
     */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    public Object NoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        String msg = "请求错误，接口路径错误！";
        log.error("系统异常, 异常原因: [{}], 接口地址: [{}]", msg, getRequestUrl(request));
        log.error("这是具体异常原因: ", e);
        return ResponseUtil.fail(502, msg);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Object badArgumentHandler(IllegalArgumentException e, HttpServletRequest request) {
        log.error("系统异常, 异常原因: [{}], 接口地址: [{}]", e.getMessage(), getRequestUrl(request));
        log.error("这是具体异常原因: ", e);
        return ResponseUtil.badArgumentValue();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public Object badArgumentHandler(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.error("系统异常, 异常原因: [{}], 接口地址: [{}]", e.getMessage(), getRequestUrl(request));
        log.error("这是具体异常原因: ", e);
        return ResponseUtil.badArgumentValue();
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Object badArgumentHandler(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.error("系统异常, 异常原因: [{}], 接口地址: [{}]", e.getMessage(), getRequestUrl(request));
        log.error("这是具体异常原因: ", e);
        return ResponseUtil.badArgumentValue();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public Object badArgumentHandler(ValidationException e, HttpServletRequest request) {
        StringBuilder message = new StringBuilder();

        if (e instanceof ConstraintViolationException) {
            ConstraintViolationException exs = (ConstraintViolationException) e;
            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
               message.append(((PathImpl) item.getPropertyPath()).getLeafNode().getName() + item.getMessage());
            }
        }
        log.error("系统异常, 异常原因: [{}], 接口地址: [{}]", message.toString(), getRequestUrl(request));
        log.error("这是具体异常原因: ", e);
        return ResponseUtil.fail(501, message.toString());
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public Object HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException  e, HttpServletRequest request){
        String error = "不支持: ".concat(e.getMethod()).concat(" 请求方式");
        log.error("系统异常, 异常原因: [{}], 接口地址: [{}]", error, getRequestUrl(request));
        return ResponseUtil.fail(501, error);
    }
    /**
     * 请求参数异常
     * @param e
     * @return
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Object missingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request){
        String error = String.format("参数名称: %s 不能为空，类型: %s", e.getParameterName(), e.getParameterType());
        log.error("系统异常, 异常原因: [{}], 接口地址: [{}]", error, getRequestUrl(request));
        return ResponseUtil.fail(401, error);
    }
    /** 自定义 处理  MethodArgumentNotValidException 参数校验错误
     * @param e
     * @return 根据自定义返回结果 返回对应内容
     * 如果 使用的是 org.hibernate.validator jar 的话 使用:
     * MethodArgumentNotValidException.class 捕捉参数校验异常
     * 如果是 SpringBoot默认的校验  使用:
     * org.springframework.validation.BindException.class 捕捉参数校验异常
     * 我这里使用的是  SpringBoot默认的
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public Object BindException(MethodArgumentNotValidException e, HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        for (ObjectError error: e.getBindingResult().getAllErrors()) {
            sb.append(error.getDefaultMessage());
            sb.append("、");
        }
        String errorMsg = sb.substring(0,sb.length() - 1).toString();
        log.error("系统异常, 异常原因: [{}], 接口地址: [{}]", errorMsg, getRequestUrl(request));
        log.error("这是具体异常原因: ", e);
        return ResponseUtil.fail(401, errorMsg);
    }
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Object ServiceException(ServiceException e, HttpServletRequest request) {
        log.error("系统异常, 异常原因: [{}], 接口地址: [{}]", e.getMessage(), getRequestUrl(request));
        log.error("这是具体异常原因: ", e);
        return ResponseUtil.fail(502, e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Object RuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("系统异常, 异常原因: [{}], 接口地址: [{}]", e.getMessage(), getRequestUrl(request));
        log.error("这是具体异常原因: ", e);
        return ResponseUtil.fail(501, "系統異常", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object seriousHandler(Exception e, HttpServletRequest request) {
        log.error("系统异常, 异常原因: [{}], 接口地址: [{}]", e.getMessage(), getRequestUrl(request));
        log.error("这是具体异常原因: ", e);
        return ResponseUtil.serious();
    }
}
