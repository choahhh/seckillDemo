package com.example.seckilldemo.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@EnableAsync
@DependsOn("threadConfig")
@Slf4j
public class ExecutorConfig {



    @Bean(name = "threadExecutor")
    @Primary
    public ThreadPoolTaskExecutor threadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(ThreadConfig.getInstance().getThreadCorePoolSize());
        executor.setMaxPoolSize(ThreadConfig.getInstance().getThreadMaxPoolSize());
        executor.setQueueCapacity(ThreadConfig.getInstance().getThreadQueueCapacity());
        executor.setThreadNamePrefix("thread");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        Thread.UncaughtExceptionHandler exceptionHandler = (t, ex) -> {
            log.error("异步线程执行失败。异常信息[{}] : ", ex.getMessage(), ex);
        };
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);

//         ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtil.getPool(4, 8, 5000);

        return executor;
    }


    @Bean(name = "excelExecutor")
    public ThreadPoolTaskExecutor excelExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(ThreadConfig.getInstance().getThreadMaxPoolSize());
        executor.setQueueCapacity(ThreadConfig.getInstance().getThreadQueueCapacity());
        executor.setThreadNamePrefix("excel");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean(name = "rockMqExecutor")
    public ThreadPoolTaskExecutor rockMqExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(ThreadConfig.getInstance().getThreadMaxPoolSize());
        executor.setQueueCapacity(ThreadConfig.getInstance().getThreadQueueCapacity());
        executor.setThreadNamePrefix("rocketMq");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }


    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.initialize();
        return taskScheduler;
    }
}
