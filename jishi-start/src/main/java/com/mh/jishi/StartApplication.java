package com.mh.jishi;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
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


@SpringBootApplication(scanBasePackages = {"com.mh.jishi"})
@MapperScan({"com.baomidou.mybatisplus.samples.quickstart.mapper", "com.mh.jishi,mapper"})
@EnableAsync
@EnableTransactionManagement
@EnableScheduling
@Slf4j
public class StartApplication {
    public static void main(String[] args) throws Exception {
        // ES设置
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(StartApplication.class, args);

        Environment environment = BeanUtil.getBean(Environment.class);
        // 服务端口
        String port = environment.getProperty("server.port");
        // 服务访问路径
        String contextPath = environment.getProperty("server.servlet.context-path");
        // 是否开启 Druid监控
        String statViewServlet = environment.getProperty("spring.datasource.druid.statViewServlet.enabled");
        if(StringUtils.isBlank(statViewServlet)){
            statViewServlet = "false";
        }
        log.info("是否开启 Druid监控： 【{}】", statViewServlet);
        if(statViewServlet.equals("true")){
            String DruidUserName = environment.getProperty("spring.datasource.druid.statViewServlet.login-username");
            String DruidPassword = environment.getProperty("spring.datasource.druid.statViewServlet.login-username");
            log.info("Druid访问路径： 【{}】, 登录用户名：【{}】， 密码：【{}】", contextPath, DruidUserName, DruidPassword);
        }
        log.info("============服务启动成功, 访问路径:【{}/druid】, 端口号: 【{}】============", contextPath, port);

}
