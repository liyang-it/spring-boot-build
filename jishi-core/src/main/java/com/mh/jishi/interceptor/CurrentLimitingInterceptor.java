package com.mh.jishi.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.mh.jishi.annotation.CurrentLimitingRequest;
import com.mh.jishi.config.CurrentLimitingRequestException;
import com.mh.jishi.constants.RedisKeyPrefix;
import com.mh.jishi.util.BeanUtil;
import com.mh.jishi.util.IpUtil;
import com.mh.jishi.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * <h2>接口限流拦截器处理</h2>
 * <p>
 *
 * </p>
 *
 * @author Evan <1922802352@qq.com>
 * @since 2022年06月23日 9:26
 */
@Slf4j
public class CurrentLimitingInterceptor implements HandlerInterceptor {
	private final RedisUtil redisUtil;

	public CurrentLimitingInterceptor() {
		this.redisUtil = BeanUtil.getBean(RedisUtil.class);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean result = true;
		if (handler instanceof HandlerMethod) {
			HandlerMethod method = (HandlerMethod) handler;
			CurrentLimitingRequest c = method.getMethodAnnotation(CurrentLimitingRequest.class);
			if (c != null) {
				// 当前请求IP
				String nowIp = IpUtil.getIpAddr(request);
				String nowUri = request.getRequestURI();
				String key = String.format(RedisKeyPrefix.CurrentLimitingRequest, nowUri, nowIp);
				if (redisUtil.hasKey(key)) {
					throw new CurrentLimitingRequestException(
							String.format("操作繁忙，请等待 %s 秒后重试", redisUtil.getExpire(key)));
				} else {
					redisUtil.set(key, c.seconds(), c.seconds());
				}

			}
		}

		return result;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
}
