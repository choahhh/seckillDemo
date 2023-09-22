package com.example.seckilldemo.data;

import com.example.seckilldemo.entity.vo.OrderMessage;

import java.util.concurrent.LinkedBlockingQueue;

public class OrderQueue {

    private static LinkedBlockingQueue<OrderMessage> orderQueue = new LinkedBlockingQueue<>();

    public static boolean  addMessage(OrderMessage orderMessage){
        return orderQueue.offer(orderMessage);
    }

    public static OrderMessage  getMessage() throws InterruptedException {
        return orderQueue.take();
    }
}
