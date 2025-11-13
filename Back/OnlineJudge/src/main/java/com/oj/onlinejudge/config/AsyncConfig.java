package com.oj.onlinejudge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步任务线程池配置。
 * 说明：
 * - 统一提供名为 "asyncExecutor" 的线程池供 @Async 使用。
 * - 可根据业务量调整核心/最大线程数与队列容量，避免阻塞或资源浪费。
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 自定义异步执行器。
     * 返回：Spring 管理的 ThreadPoolTaskExecutor，线程名前缀为 "async-"。
     */
    @Bean("asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}
