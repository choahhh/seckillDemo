package com.example.seckilldemo.config;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @Author: wangzsky
 * @Date: 2019/10/8 16:12
 */

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "thread")
@Data
public class ThreadConfig {

    private static ThreadConfig _this;


    public static ThreadConfig getInstance() {
        return _this;
    }



    private int threadCorePoolSize;
    private int threadMaxPoolSize;
    private int threadQueueCapacity;


    @PostConstruct
    public void init() {
        _this = this;
        log.info("dms配置：{}", this);

    }


}