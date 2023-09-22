package com.example.seckilldemo.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.seckilldemo.dao.OrderMapper;
import com.example.seckilldemo.data.RedisKey;
import com.example.seckilldemo.entity.enums.OrderStatus;
import com.example.seckilldemo.entity.enums.OrderType;
import com.example.seckilldemo.entity.po.Goods;
import com.example.seckilldemo.entity.po.Order;
import com.example.seckilldemo.order.AbstractOrderTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Slf4j
@CacheConfig(cacheNames = {"seckill"})
public class SeckillOrderServiceImpl extends AbstractOrderTypeService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String getType() {
        return OrderType.SECKILL.getValue();
    }

    @Override
    public Boolean isPlace(Goods goods) {
        if (goods == null) {
            return false;
        }
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        long now = Timestamp.valueOf(LocalDateTime.now()).getTime();
        if (startDate.getTime() > now || endDate.getTime() < now) {
            log.error("该货物已经无法购买！goods=》{}", goods);
            return false;
        }

        return true;
    }

    //判断下状态
    @Override
    @Cacheable(key = "'selectOrder_' + #p0 + '_'+ #p1", unless = "#result == null")
    public Order selectOrderOnly(Long goodsId, Long userId) {
        //状态大于0 的 都是在进行中的订单
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getGoodsId, goodsId)
                .eq(Order::getUserId, userId).eq(Order::getType,getType()).ge(Order::getStatus,0));
        return order;
    }

    @Override
//    @CacheEvict(key = "'selectOrder_' + #p0.goodsId + '_'+ #p0.userId")
    public void deleteOrderOnly(Order order) {
        orderMapper.delete(new LambdaQueryWrapper<Order>().eq(Order::getGoodsId, order.getGoodsId())
                .eq(Order::getUserId, order.getUserId()).eq(Order::getType, getType())
                .eq(Order::getId,order.getId()));
    }

    @Override
    public void updateOrder(Order order) {
        order.updateById();
        //如果订单状态时作废的 就清除redis缓存
        if(OrderStatus.isDeleteRedis(order.getStatus())) {
            deleteRedisOrder(order.getGoodsId(), order.getUserId());
        }
    }

    @Override
    public void deleteRedisOrder(Long goodsId, Long userId) {
        String orderKey = RedisKey.getOrderKey(goodsId, userId);
        redisTemplate.delete(orderKey);
    }




}
