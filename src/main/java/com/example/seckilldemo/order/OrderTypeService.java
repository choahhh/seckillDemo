package com.example.seckilldemo.order;

import com.example.seckilldemo.entity.po.Goods;
import com.example.seckilldemo.entity.po.Order;

public interface OrderTypeService {


    String getType();

    Boolean isPlace(Goods goods);

    Order selectOrderOnly(Long goodId, Long userId);

    void deleteOrderOnly(Order order);

    void save(Order order);

    void deleteRedisOrder(Long goodsId, Long userId);

    void updateOrder(Order order);
}
