package com.example.seckilldemo.order.impl;

import com.example.seckilldemo.entity.enums.OrderType;
import com.example.seckilldemo.entity.po.Order;
import com.example.seckilldemo.order.AbstractOrderTypeService;
import org.springframework.stereotype.Service;

@Service
public class CommonOrderServiceImpl extends AbstractOrderTypeService {
    @Override
    public String getType() {
        return OrderType.COMMON.getValue();
    }


    @Override
    public void deleteOrderOnly(Order order) {

    }

    @Override
    public void deleteRedisOrder(Long goodsId, Long userId) {

    }

    @Override
    public void updateOrder(Order order) {

    }
}
