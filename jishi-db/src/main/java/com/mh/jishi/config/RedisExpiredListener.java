package com.mh.jishi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author ani
 * @Description 监听redis key 过期事件
 * @CreateTime 2021-08-19 上午 11:46
 **/
@Component
public class RedisExpiredListener extends KeyExpirationEventMessageListener {
    private Logger log = LoggerFactory.getLogger(RedisExpiredListener.class);

    private java.util.concurrent.atomic.AtomicInteger AtomicInteger = new AtomicInteger(0);

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(50);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(50, 100, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);

    public RedisExpiredListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    // 有key失效就会调用message方法 返回key
    @Override
    public void onMessage(Message message, byte[] bytes) {
        String key = new String(message.getBody(), StandardCharsets.UTF_8);
        // 业务逻辑
        log.info("监听到redis, 键: {} 数据过期重新刷新", key);
        if(key.equals("home_data")){
            log.info("刷新首页redis缓存,key: {} ", key);
        }
    }
}
