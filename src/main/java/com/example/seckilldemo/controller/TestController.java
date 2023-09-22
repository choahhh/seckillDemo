package com.example.seckilldemo.controller;

import com.example.seckilldemo.dao.GoodsMapper;
import com.example.seckilldemo.data.RedisKey;
import com.example.seckilldemo.entity.enums.OrderType;
import com.example.seckilldemo.entity.po.Order;
import com.example.seckilldemo.entity.vo.FormUserDetails;
import com.example.seckilldemo.order.OrderTypeService;
import com.example.seckilldemo.order.factory.OrderTypeFactory;
import com.example.seckilldemo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@RestController
public class TestController {


    @Autowired
    private GoodsMapper seckillGoodsMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;
    @PostConstruct
    public void init(){

        redisTemplate.opsForValue().increment(RedisKey.getAdvanceCount(2l), 1);

        Integer count = (Integer) redisTemplate.opsForValue().get(RedisKey.getAdvanceCount(1l));
        if (count != null && count > 0) {
            redisTemplate.opsForValue().decrement(RedisKey.getAdvanceCount(1l));
        }
        redisTemplate.delete("isStockEmpty:1");

//        SeckillGoods seckillGoods = new SeckillGoods();
//        seckillGoods.setSeckillPrice(new BigDecimal(2.5));
//        seckillGoods.setStockCount(50);
//        seckillGoods.setGoodsId(3L);
//        seckillGoods.setEndDate(new Date());
//        seckillGoods.setStartDate(new Date());
////        seckillGoodsMapper.insert(seckillGoods);
//        seckillGoods.insert();
    }

    @RequestMapping("/te")
    public void init1(HttpServletRequest request,FormUserDetails userDetails){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        OrderTypeService orderTypeService = OrderTypeFactory.getService(OrderType.SECKILL.getValue());
        Order seckillOrder = orderTypeService.selectOrderOnly(2l, 1l);
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            System.out.println(cookie.getName()+":"+cookie.getValue());
        }
        System.out.println(principal);


//        redisTemplate.delete("isStockEmpty:1");
//
//        SeckillGoods seckillGoods = new SeckillGoods();
//        seckillGoods.setSeckillPrice(new BigDecimal(2.5));
//        seckillGoods.setStockCount(50);
//        seckillGoods.setGoodsId(3L);
//        seckillGoods.setEndDate(new Date());
//        seckillGoods.setStartDate(new Date());
////        seckillGoodsMapper.insert(seckillGoods);
//        seckillGoods.insert();
    }

//    @GetMapping("/t")
//    public void init2(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        Object principal = authentication.getPrincipal();
//        if(principal != null) {
//            System.out.println(principal);
//        }
//
//
//    }
}
