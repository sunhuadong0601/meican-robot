package com.alex.meican.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author sunhuadong
 * @date 2020/5/22 12:16 上午
 */
@Configuration
public class TaskExecutorConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // 核心线程数
        taskExecutor.setCorePoolSize(2);
        // 最大线程数
        taskExecutor.setMaxPoolSize(15);
        // 队列大小
        taskExecutor.setQueueCapacity(100);
        // 线程最大空闲时间
        taskExecutor.setKeepAliveSeconds(300);
        // 拒绝策略
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程名称前缀
        taskExecutor.setThreadNamePrefix("pool-");
        taskExecutor.initialize();
        return taskExecutor;
    }
}
