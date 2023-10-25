package com.example.seckilldemo.controller;


import com.example.seckilldemo.aop.annotation.Limit;
import com.example.seckilldemo.data.RespBeanEnum;
import com.example.seckilldemo.entity.dto.CreateOrderDto;
import com.example.seckilldemo.entity.dto.GoodsDto;
import com.example.seckilldemo.entity.enums.OrderType;
import com.example.seckilldemo.entity.po.Goods;
import com.example.seckilldemo.entity.po.SeckillRule;
import com.example.seckilldemo.entity.po.User;
import com.example.seckilldemo.order.OrderTypeService;
import com.example.seckilldemo.order.factory.OrderTypeFactory;
import com.example.seckilldemo.service.*;
import com.example.seckilldemo.util.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/seckill/order")
@Slf4j
public class OrderController {


    @Autowired
    private SeckillService seckillService;

    @Autowired
    private OrderService orderService;


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private UserService userService;

    @Autowired
    private SeckillRuleService seckillRuleService;


    @RequestMapping("/findAllOrderType")
    public AjaxResult findAllOrderType() {
        return AjaxResult.data(OrderType.findAllType());
    }

    @RequestMapping("/buy")
    @ResponseBody
    @Limit(key = "placeOrder", timeOut = 2, timeUnit = TimeUnit.SECONDS, permitsPerSecond = 300)
    public AjaxResult placeOrder(@RequestBody CreateOrderDto orderDto) {
        Long goodsId = orderDto.getGoodsId();
        Long userId = orderDto.getUserId();
        User userById = userService.findUserNoBanlanceById(userId);
        if (userById == null) {
            return AjaxResult.error(RespBeanEnum.SESSION_NOT_EXIT.getMessage());
        }


        Goods goods = goodsService.findGoodsNoCountById(goodsId);
        if (goods == null) {
            return AjaxResult.error(RespBeanEnum.GOOD_NOT_EXISTS.getMessage());
        }
        GoodsDto goodsDto = new GoodsDto(goods.getId(), goods.getPrice(), goods.getName());
        String type = OrderType.COMMON.getValue();
        SeckillRule rule = seckillRuleService.getRule();
        if (goods.getIskill() == 0 && rule != null && rule.getStatus() == 0) {
            //秒杀商品
            //商品价格设置为秒杀价格
            goodsDto.setGoodsPrice(rule.getSeckillPrice());
            type = OrderType.SECKILL.getValue();
        }
        OrderTypeService orderTypeService = OrderTypeFactory.getService(type);
        if (!orderTypeService.isPlace(goods, rule)) {
            return AjaxResult.error(RespBeanEnum.NOT_IN_TIME.getMessage());
        }
        return seckillService.seckillOrder(orderTypeService, goodsDto, 1, userId);
    }


}
