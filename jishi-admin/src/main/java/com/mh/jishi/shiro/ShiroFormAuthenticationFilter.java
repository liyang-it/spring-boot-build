package com.mh.jishi.shiro;

import com.alibaba.fastjson.JSONObject;
import com.mh.jishi.util.ResponseUtil;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * <h2>重写Shiro处理未登录请求操作</h2>
 * <p>
 * 未登录状态发送的请求都会重定向，导致前端无法捕捉重定向后的消息，重写改为直接输出json不重定向
 * </p>
 *
 * @author
 * @since
 */

public class ShiroFormAuthenticationFilter extends FormAuthenticationFilter {
	private static final Logger log = LoggerFactory.getLogger(ShiroFormAuthenticationFilter.class);
	
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		if (this.isLoginRequest(request, response)) {
			if (this.isLoginSubmission(request, response)) {
				if (log.isTraceEnabled()) {
					log.trace("Login submission detected.  Attempting to execute login.");
				}
				
				return this.executeLogin(request, response);
			} else {
				if (log.isTraceEnabled()) {
					log.trace("Login page view.");
				}
				
				return true;
			}
		} else {
			HttpServletRequest req = (HttpServletRequest)request;
			HttpServletResponse resp = (HttpServletResponse)response;
			if (req.getMethod().equals(RequestMethod.OPTIONS.name())) {
				resp.setStatus(HttpStatus.OK.value());
				return true;
			} else {
				if (log.isTraceEnabled()) {
					log.trace("Attempting to access a path which requires authentication.  Forwarding to the Authentication url [{}]" ,this.getLoginUrl());
				}
				// 在这里实现自己想返回的信息，其他地方和源码一样就可以了
				resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
				resp.setHeader("Access-Control-Allow-Credentials", "true");
				resp.setContentType("application/json; charset=utf-8");
				resp.setCharacterEncoding("UTF-8");
				PrintWriter out = resp.getWriter();
				out.println(JSONObject.toJSONString(ResponseUtil.unlogin()));
				out.flush();
				out.close();
				return false;
			}
		}
	}
}
