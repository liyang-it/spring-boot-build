package com.mh.jishi.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <h2>解决shiro跨域</h2>
 * <p>
 *
 * </p>
 *
 * @author Evan <1922802352@qq.com>
 * @since 2022年12月15日 17:09
 */
@Slf4j
@Configuration
public class CROSUserFilter extends UserFilter {
	
	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
			setHeader(httpRequest, httpResponse);
			return true;
		}
		
		return super.preHandle(request, response);
	}
	
	private void setHeader(HttpServletRequest request, HttpServletResponse response) {
		//跨域的header设置
		response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
		response.setHeader("Access-Control-Allow-Methods", request.getMethod());
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
		//防止乱码，适用于传输JSON数据
		response.setHeader("Content-Type", "application/json;charset=UTF-8");
		response.setStatus(HttpStatus.OK.value());
	}
}
