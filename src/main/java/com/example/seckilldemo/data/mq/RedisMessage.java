package com.example.seckilldemo.data.mq;

import com.alibaba.fastjson.JSON;
import com.example.seckilldemo.entity.vo.OrderMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;

@Service
public class RedisMessage implements MqMessage{

    @Autowired
    private RedisTemplate redisTemplate;
    private static LinkedBlockingQueue<OrderMessage> orderQueue = new LinkedBlockingQueue<>();
    @Override
    public synchronized  boolean addMessage(OrderMessage orderMessage) {
        String jsonString = JSON.toJSONString(orderMessage);
        try {
            redisTemplate.opsForList().leftPush("order", jsonString);
            notifyAll();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public synchronized OrderMessage getMessage() throws InterruptedException {
        while(redisTemplate.opsForList().size("order") <= 0) {
            wait();
        }
        String o = (String) redisTemplate.opsForList().rightPop("order");
        notifyAll();
        return JSON.parseObject(o,OrderMessage.class);
    }
}
