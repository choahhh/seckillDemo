package com.example.seckilldemo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckilldemo.entity.po.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    Order selectOrderInfo(@Param("orderId") Long orderId, @Param("userId") Long userId);
}
