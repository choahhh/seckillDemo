package com.example.seckilldemo.data;

import com.example.seckilldemo.data.mq.MqMessage;
import com.example.seckilldemo.entity.vo.OrderMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class OrderQueue {

    @Autowired
    MqMessage mqMessage;
    private static OrderQueue _this;

    @PostConstruct
    public void init() {
        _this = this;

    }
    public static OrderQueue getInstance() {
        return _this;
    }
    public boolean  addMessage(OrderMessage orderMessage){
        return mqMessage.addMessage(orderMessage);
    }

    public  OrderMessage  getMessage() throws InterruptedException {
        return mqMessage.getMessage();
    }


}
