package com.example.seckilldemo.data.mq;

import com.example.seckilldemo.entity.vo.OrderMessage;

import java.util.concurrent.LinkedBlockingQueue;

//@Service
public class QueueMessage implements MqMessage{
    private static LinkedBlockingQueue<OrderMessage> orderQueue = new LinkedBlockingQueue<>();
    @Override
    public boolean addMessage(OrderMessage orderMessage) {
        return orderQueue.offer(orderMessage);
    }

    @Override
    public OrderMessage getMessage() throws InterruptedException {
        return orderQueue.take();
    }
}
