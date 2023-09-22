package com.example.seckilldemo.order;

import com.example.seckilldemo.dao.OrderMapper;
import com.example.seckilldemo.entity.po.Order;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractOrderTypeService implements OrderTypeService{

    @Autowired
    private OrderMapper orderMapper;
    @Override
    public Order selectOrderOnly(Long goodsId, Long userId) {
       //如果不是秒杀订单 不需要判断 所以直接返回null
        return null;
    }

    @Override
    public void save(Order order){
        orderMapper.insert(order);
    }



}
