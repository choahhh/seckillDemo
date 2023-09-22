package com.example.seckilldemo.entity.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  放入RabbitMQ的秒杀消息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderMessage {
    private FormUserDetails user;
    private Long goodId;
    private String type;
    private Integer goodsCount;
}
