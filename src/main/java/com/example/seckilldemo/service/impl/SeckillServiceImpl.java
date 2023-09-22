package com.example.seckilldemo.service.impl;

import com.example.seckilldemo.data.OrderQueue;
import com.example.seckilldemo.data.RespBeanEnum;
import com.example.seckilldemo.entity.enums.OrderStatus;
import com.example.seckilldemo.entity.po.Goods;
import com.example.seckilldemo.entity.po.GoodsDetail;
import com.example.seckilldemo.entity.po.Order;
import com.example.seckilldemo.entity.po.OrderDetail;
import com.example.seckilldemo.entity.vo.FormUserDetails;
import com.example.seckilldemo.entity.vo.OrderMessage;
import com.example.seckilldemo.order.OrderTypeService;
import com.example.seckilldemo.order.factory.OrderTypeFactory;
import com.example.seckilldemo.service.GoodsDetailService;
import com.example.seckilldemo.service.GoodsService;
import com.example.seckilldemo.service.SeckillService;
import com.example.seckilldemo.util.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsDetailService goodsDetailService;

    @Override
    public AjaxResult seckillOrder(String type,Long goodId, Integer count,FormUserDetails user) {
        OrderTypeService orderTypeService = OrderTypeFactory.getService(type);
        //TODO:按需改进代码
//        boolean check = orderService.checkPath(user,goodsId,path);
//        if (!check){
//            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
//        }
        //看是否存在重复订单 如果能设置值 说明目前该用户没有订单在生成，后面排队生成订单时 删除
        //一次性点2次购买  设置一个10分钟的 key 如果能设置成功 表示没有人购买 继续后台操作，如果设置失败 表示有重复订单， 后面一个查库存的 类
        Order order = orderTypeService.selectOrderOnly(goodId, user.getId());
        if (order != null ) {
            return AjaxResult.error(RespBeanEnum.REPEAT_ERROR.getMessage());
        }
        //避免每次去查数据库 或者redis goodId是该类型商品的id
//        if (EmptyStockMap.containsGoodId(goodId)) {
//            return AjaxResult.error(RespBeanEnum.EMPTY_STOCK.getMessage());
//        }

        //先预先减库存，如果不够减就返回错误
        boolean b = goodsService.advanceCalSeckillCount(goodId,count);
        if (!b) {
//            EmptyStockMap.addGoodId(goodId);
            return AjaxResult.error(RespBeanEnum.EMPTY_STOCK.getMessage());
        }
        //可以根据类型放入不同的队列
        OrderMessage seckillMessage = new OrderMessage(user, goodId,type,count);
        // 优化 可以通过rabbitmq发送消息
        OrderQueue.addMessage(seckillMessage);
        return AjaxResult.success();

    }

    @Override
    @Transactional
    public void secKill(FormUserDetails user, Long goodsId,Integer goodsCount,String type) {

        //减少库存
        boolean b = goodsService.subtractSeckillCount(goodsId,goodsCount);
        if (!b) {
            log.error("减少库存失败!goodsId==>{},user=>{}",goodsId,user);
            //可能redis被清空 引起的
            return;
        }
        //goods和秒杀的商品关系是，goods是秒杀商品的detail 后续可能会有其他商品 比如特惠商品
        //订单关系同理
        //生成订单
        Goods goods = goodsService.getById(goodsId);
        GoodsDetail goodsDetail = null;
        if(goods != null) {
            goodsDetail = goodsDetailService.getById(goods.getGoodsId());
        }
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setGoodsId(goodsId);
        orderDetail.setGoodsName(goodsDetail.getGoodsName());
        orderDetail.setGoodsCount(goodsCount);
        orderDetail.setGoodsPrice(goods.getSeckillPrice());
        orderDetail.setOrderChannel(1);
        orderDetail.insert();
        //再生成订单
        OrderTypeService orderTypeService = OrderTypeFactory.getService(type);
        Order order = new Order();
        order.setUserId(user.getId());
        order.setOrderId(orderDetail.getId());
        order.setGoodsId(goodsId);
        order.setType(type);
        order.setStatus(OrderStatus.NEW.getValue());
        order.setCreateDate(LocalDateTime.now());
        order.setUpdateDate(LocalDateTime.now());
        orderTypeService.save(order);
    }



}
