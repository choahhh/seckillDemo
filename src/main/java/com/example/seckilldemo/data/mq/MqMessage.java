package com.example.seckilldemo.data.mq;

import com.example.seckilldemo.entity.vo.OrderMessage;

public interface MqMessage {
      boolean  addMessage(OrderMessage orderMessage);
      OrderMessage  getMessage() throws InterruptedException;
}
