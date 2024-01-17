package com.mh.jishi;

import com.mh.jishi.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.spring.boot.autoconfigure.ShiroAnnotationProcessorAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;


@ImportResource(locations = {"classpath:spring/application-spring.xml"})
@DependsOn(value = "BeanUtil")
@SpringBootApplication(scanBasePackages = {"com.mh.jishi"}, exclude = {ShiroAnnotationProcessorAutoConfiguration.class})
@MapperScan({"com.baomidou.mybatisplus.samples.quickstart.mapper", "com.mh.jishi.mapper"})
@EnableAsync
@EnableTransactionManagement
@EnableScheduling
@Slf4j
public class StartApplication {
	public static void main(String[] args) throws Exception {
		log.info("=================  服务启动中...  =================");
		System.setProperty("es.set.netty.runtime.available.processors", "false");
		SpringApplication.run(StartApplication.class, args);

		Environment environment = BeanUtil.getBean(Environment.class);
		// 服务端口
		String port = environment.getProperty("server.port");
		// 服务访问路径
		String contextPath = environment.getProperty("server.servlet.context-path");
		// 是否开启 Druid监控
		String statViewServlet = environment.getProperty("spring.datasource.druid.stat-view-servlet.enabled");
		if (StringUtils.isBlank(statViewServlet)) {
			statViewServlet = "false";
		}
		log.info("是否开启 Druid 数据库 监控：【{}】", statViewServlet);
		if (statViewServlet.equals("true")) {
			String DruidUserName = environment.getProperty("spring.datasource.druid.stat-view-servlet.login-username");
			String DruidPassword = environment.getProperty("spring.datasource.druid.stat-view-servlet.login-password");
			log.info("Druid访问路径：【http://127.0.0.1:{}{}/druid/login.html】", port, contextPath);
			log.info("Druid 登录用户名：【{}】", DruidUserName);
			log.info("Druid 登录密码：【{}】", DruidPassword);
		}
		// 是否开启 actuator 监控
		String statActuator = environment.getProperty("management.endpoint.health.show-details");
		if (StringUtils.isNotBlank(statActuator)) {
			statActuator = "true";
		} else {
			statActuator = "false";
		}
		log.info("是否开启 Actuator 程序监控：【{}】", statActuator);
		
		if (statActuator.equals("true")) {
			log.info("Actuator访问路径：【{}/actuator】", contextPath);
		}
		log.info("是否开启 Druid监控：【{}】", statViewServlet);
		log.info("服务访问路径：【http://127.0.0.1:{}{}】", port, contextPath);
		log.info("服务访问端口：【{}】", port);
		log.info("服务启动时间：【{}】", LocalDateTime.now());
		log.info("=================  服务启动成功  =================");
	}
}
