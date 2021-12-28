package com.mh.jishi.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 用于处理 程序重启 之后 继续处理延迟任务
 */
@Component
@Order(1)
public class TaskStartupRunner implements ApplicationRunner {

    private static Logger log = LoggerFactory.getLogger(TaskStartupRunner.class);

    @Autowired
    private TaskService taskService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }


}