package com.example.seckilldemo.service.impl;

import com.example.seckilldemo.service.GoodsService;
import com.example.seckilldemo.service.RedisSync;
import com.example.seckilldemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class RedisSyncImpl implements RedisSync {

    @Autowired
    private UserService userService;
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void init(){
        goodsService.syncCount(null);
        userService.syncBalance(null);
        redisTemplate.delete("order");
    }



}
