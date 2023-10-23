package com.example.seckilldemo.order;

import com.example.seckilldemo.entity.po.Goods;
import com.example.seckilldemo.entity.po.Order;
import com.example.seckilldemo.entity.po.SeckillRule;

public interface OrderTypeService {


    String getType();

    Boolean isPlace(Goods goods, SeckillRule seckillRule);

    Order selectOrderOnly(Long goodId, Long userId);

    void deleteOrderOnly(Order order);

    void save(Order order);

    void deleteRedisOrder(Long goodsId, Long userId);

    void updateOrder(Order order);

    Boolean selectOrderOnlyLock(Long id, Long userId);

    Boolean releaseOrderOnlyLock(Long id, Long userId);

}
