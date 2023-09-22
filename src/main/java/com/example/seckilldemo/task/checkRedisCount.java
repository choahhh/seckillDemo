package com.example.seckilldemo.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

//检查redis的库存 跟 数据库的是否能对上 对不上清空，保证 redis预增加的库存量 要小于 数据库的库存量
@Component
public class checkRedisCount {
    //先算 redis 再差数据库
//    3 42
    @Autowired
    private RedisTemplate redisTemplate;

//    @Scheduled()
    public void correctRedisCache() {
        Set keys = redisTemplate.keys("advance:count*");
    }


}
