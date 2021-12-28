package com.mh.jishi.task;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;

/**
 * 延迟任务队列
 */
@Component
public class TaskService {
    private TaskService taskService;
    private DelayQueue<Task> delayQueue =  new DelayQueue<Task>();

    @PostConstruct
    private void init() {
        // 当前类初始化执行这个方法, 循环取 队列中的任务
        taskService = this;

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Task task = delayQueue.take();
                        task.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void addTask(Task task){
        if(delayQueue.contains(task)){
            return;
        }
        delayQueue.add(task);
    }

    public void removeTask(Task task){
        delayQueue.remove(task);
    }

}
