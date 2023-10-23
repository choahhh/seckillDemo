package com.example.seckilldemo.service.impl;

import com.example.seckilldemo.data.OrderQueue;
import com.example.seckilldemo.data.RedisKey;
import com.example.seckilldemo.data.RespBeanEnum;
import com.example.seckilldemo.entity.dto.GoodsDto;
import com.example.seckilldemo.entity.enums.OrderStatus;
import com.example.seckilldemo.entity.po.Order;
import com.example.seckilldemo.entity.po.OrderDetail;
import com.example.seckilldemo.entity.po.PayLog;
import com.example.seckilldemo.entity.vo.OrderMessage;
import com.example.seckilldemo.order.OrderTypeService;
import com.example.seckilldemo.service.GoodsService;
import com.example.seckilldemo.service.SeckillService;
import com.example.seckilldemo.service.UserService;
import com.example.seckilldemo.util.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {


    @Autowired
    private GoodsService goodsService;

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;



    @Override
    public AjaxResult seckillOrder(OrderTypeService orderTypeService, GoodsDto goodsDto, Integer count, Long userId) {
        //TODO:按需改进代码
        //一次性点2次购买  设置一个10分钟的 key 如果能设置成功 表示没有人购买 继续后台操作，如果设置失败 表示有重复订单， 后面一个查库存的 类
        Boolean aBoolean = orderTypeService.selectOrderOnlyLock(goodsDto.getId(), userId);
        Order order = orderTypeService.selectOrderOnly(goodsDto.getId(), userId);
        if (!aBoolean || order != null) {
            return AjaxResult.error(RespBeanEnum.REPEAT_ERROR.getMessage());
        }

        BigDecimal balance = goodsDto.getGoodsPrice().multiply(new BigDecimal(count + ""));

        //先判断 数量和金额够不够减 减少redis操作
        AjaxResult result = advanceCheck(userId, goodsDto.getId(), balance, count);
        if(!result.isSuccess()){
            return result;
        }

        //先redis操作
        Boolean byRedis = userService.updateBalanceByRedis(userId, balance.negate());
        if(!byRedis) {
            return AjaxResult.error(RespBeanEnum.NO_MONEY.getMessage());
        }
        Boolean goodRedis = goodsService.updateCountByRedis(goodsDto.getId(),0 - count);
        if(!goodRedis) {
            userService.updateBalanceByRedis(userId, balance);
            return AjaxResult.error(RespBeanEnum.EMPTY_STOCK.getMessage());
        }
        //可以根据类型放入不同的队列
        OrderMessage seckillMessage = new OrderMessage(userId, goodsDto,orderTypeService.getType(),count,balance);
        // 优化 可以通过rabbitmq发送消息
        OrderQueue.getInstance().addMessage(seckillMessage);
        return AjaxResult.success();

    }


    private AjaxResult advanceCheck(Long userId,Long goodsId,BigDecimal balance,Integer count){
        String advanceBalance = RedisKey.getAdvanceBalance(userId);
        Double curAmount = (double) 0;
        Object o = redisTemplate.opsForValue().get(advanceBalance);
        if(o instanceof  Integer) {
            curAmount = ((Integer) o).doubleValue();
        } else {
            curAmount = (Double) o;
        }
        if(curAmount - balance.doubleValue() < -0.001) {
            log.error("redis 余额不足!可执行同步处理!userId=>{},curAmount=>{},balance=>{},chazhi=>{}",
                    userId,curAmount,balance.negate().doubleValue(),curAmount - balance.doubleValue());
            return AjaxResult.error(RespBeanEnum.NO_MONEY.getMessage());
        }
        //判断数量
        String advanceCount = RedisKey.getAdvanceCount(goodsId);
        Integer socketCount = (Integer) redisTemplate.opsForValue().get(advanceCount);
        if(socketCount.compareTo(count) < -0) {
            log.error("redis 数量不足!可执行同步处理!");
            return AjaxResult.error(RespBeanEnum.EMPTY_STOCK.getMessage());
        }
        return  AjaxResult.success();
    }

    @Override
    @Transactional
    public void secKill(Long userId, GoodsDto goodsDto, Integer goodsCount, OrderTypeService orderTypeService,
                           BigDecimal balance) throws Exception {

        BigDecimal balanceByDb = userService.findBalanceByDb(userId);


        //直接数据库判断保证原子性
        Long goodsId = goodsDto.getId();
        LocalDateTime now = LocalDateTime.now();
        int i = userService.updateBalanceByDb(userId, balance);
        if (i <= 0) {
            log.error("goodsId=>{},userId=>{}账户余额不足！",goodsId,userId);
            userService.syncBalance(userId);
            goodsService.updateCountByRedis(goodsDto.getId(), goodsCount);
            return;

        }

        //减库存
        boolean b = goodsService.subtractSeckillCount(goodsId, goodsCount);
        if(!b) {
            log.error("{}减库存失败！",goodsId);
            //进行同步处理
            goodsService.syncCount(goodsId);
            //回退金额
            userService.updateBalanceByRedis(userId, balance);
            //回退数据库的金额
            userService.addBalanceByDb(userId, balance);
            return;
        }



        BigDecimal remainingAmount = userService.findBalanceByDb(userId);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setGoodsId(goodsDto.getId());
        orderDetail.setGoodsName(goodsDto.getName());
        orderDetail.setGoodsCount(goodsCount);
        orderDetail.setGoodsPrice(balance);
        orderDetail.insert();
        //再生成订单
        Order order = new Order();
        order.setUserId(userId);
        order.setGoodsId(goodsId);
        order.setOrderId(orderDetail.getId());
        order.setType(orderTypeService.getType());
        order.setStatus(OrderStatus.PAY.getValue());
        order.setCreateDate(LocalDateTime.now());
        orderTypeService.save(order);

        PayLog payLog = new PayLog();
        payLog.setNote("支付\"" + order.getId() + "\"订单成功！");
        payLog.setCreateTime(now);
        payLog.setCreateUserId(userId);
        payLog.setSrcAmount(balanceByDb);
        payLog.setRemainingAmount(remainingAmount);
        payLog.setDeductionAmount("-" + balance.toString());
        payLog.insert();
    }



}
