package com.mh.jishi;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mh.jishi.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.time.LocalDateTime;


@SpringBootApplication(scanBasePackages = {"com.mh.jishi"})
@MapperScan({"com.baomidou.mybatisplus.samples.quickstart.mapper", "com.mh.jishi,mapper"})
@EnableAsync
@EnableTransactionManagement
@EnableScheduling
@Slf4j
public class StartApplication {
    public static void main(String[] args) throws Exception {
        log.info("=================  服务启动中...  =================");
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(StartApplication.class, args);
        /**
         * 下列代码，可以使用异步线程运行，不会影响程序主线程启动
         */
        Environment environment = BeanUtil.getBean(Environment.class);
        // 服务端口
        String port = environment.getProperty("server.port");
        // 服务访问路径
        String contextPath = environment.getProperty("server.servlet.context-path");
        // 是否开启 Druid监控
        String statViewServlet = environment.getProperty("spring.datasource.druid.statViewServlet.enabled");
        if (StringUtils.isBlank(statViewServlet)) {
            statViewServlet = "false";
        }
        log.info("是否开启 Druid 数据库 监控：【{}】", statViewServlet);
        if (statViewServlet.equals("true")) {
            String DruidUserName = environment.getProperty("spring.datasource.druid.statViewServlet.login-username");
            String DruidPassword = environment.getProperty("spring.datasource.druid.statViewServlet.login-username");
            log.info("Druid访问路径：【{}/druid/login.html】", contextPath);
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
