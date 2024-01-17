package com.mh.jishi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
	
	private CorsConfiguration buildConfig() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		// 1 设置访问源地址
		corsConfiguration.addAllowedOrigin("*");
		// 2 设置访问源请求头
		corsConfiguration.addAllowedHeader("*");
		// 3 设置访问源请求方法
		corsConfiguration.addAllowedMethod("*");
		// 当前跨域请求最大有效时长。这里默认30天
		long maxAge = 30 * 24 * 60 * 60;
		corsConfiguration.setMaxAge(maxAge);
		corsConfiguration.setAllowCredentials(true);
		return corsConfiguration;
	}
	
	@Bean
	CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		// 4 对接口配置跨域设置
		source.registerCorsConfiguration("/**", buildConfig());
		return new CorsFilter(source);
	}
}
