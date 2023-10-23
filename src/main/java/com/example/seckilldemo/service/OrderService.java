package com.example.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckilldemo.entity.po.Order;
import com.example.seckilldemo.util.AjaxResult;

public interface OrderService extends IService<Order> {


    Order selectOrderInfo(Long orderId, Long userId);

    AjaxResult payOrder(Order order,Long accountId, Long userId);

    void updateOrder(Order order);

    void backOrder(Integer orderState,Order order);

    /**
     * 删除秒杀订单并且删除原来的订单，只删除失败的的订单

     */
    AjaxResult deleteAllOrder(Order order);

    /**
     * 回退订单
     * @param user
     */
//    AjaxResult returnOrder(Order order);

    AjaxResult cancleOrder(Order order);
}
