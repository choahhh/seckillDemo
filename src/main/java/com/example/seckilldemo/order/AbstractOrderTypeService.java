package com.example.seckilldemo.order;

import com.example.seckilldemo.dao.OrderMapper;
import com.example.seckilldemo.entity.po.Goods;
import com.example.seckilldemo.entity.po.Order;
import com.example.seckilldemo.entity.po.SeckillRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public abstract class AbstractOrderTypeService implements OrderTypeService{

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Order selectOrderOnly(Long goodsId, Long userId) {
       //如果不是秒杀订单 不需要判断 所以直接返回null
        return null;
    }

    @Override
    public void save(Order order){
        orderMapper.insert(order);
    }


    @Override
    public Boolean isPlace(Goods goods, SeckillRule seckillRule) {
        //如果不是秒杀订单 不需要判断 所以直接返回true
        return true;
    }

    @Override
    public Boolean selectOrderOnlyLock(Long id, Long userId) {
        return true;
    }

    @Override
    public Boolean releaseOrderOnlyLock(Long id, Long userId) {
        return true;
    }
}
