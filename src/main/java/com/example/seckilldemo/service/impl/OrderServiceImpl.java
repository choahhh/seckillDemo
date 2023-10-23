package com.example.seckilldemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckilldemo.dao.OrderMapper;
import com.example.seckilldemo.data.RespBeanEnum;
import com.example.seckilldemo.entity.enums.OrderStatus;
import com.example.seckilldemo.entity.po.Account;
import com.example.seckilldemo.entity.po.Order;
import com.example.seckilldemo.entity.po.PayLog;
import com.example.seckilldemo.order.OrderTypeService;
import com.example.seckilldemo.order.factory.OrderTypeFactory;
import com.example.seckilldemo.service.AccountService;
import com.example.seckilldemo.service.GoodsService;
import com.example.seckilldemo.service.OrderService;
import com.example.seckilldemo.util.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private AccountService accountService;


    @Autowired
    private GoodsService goodsService;


    /**
     * orderId是秒杀订单的id
     *
     * @param orderId
     * @return
     */
    @Override
    public Order selectOrderInfo(Long orderId,Long userId) {
        return baseMapper.selectOrderInfo(orderId,userId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult payOrder(Order order, Long accountId, Long userId) {
        Account account = accountService.getOne(new LambdaQueryWrapper<Account>().eq(Account::getId, accountId).eq(Account::getCreateUserId, userId));
        if (account == null) {
            return AjaxResult.error(RespBeanEnum.ACCOUNT_NOT_EXIT.getMessage());
        }
       BigDecimal needPay =  order.getOrderDetail().getGoodsPrice().multiply(new BigDecimal(order.getOrderDetail().getGoodsCount() + ""));
        //直接数据库判断保证原子性
        BigDecimal remainingAmount = accountService.subtractAmount(
                accountId, needPay );
        LocalDateTime now = LocalDateTime.now();
        if (remainingAmount == null) {
            return AjaxResult.error(RespBeanEnum.NO_MONEY.getMessage());
        }
        //表示操作成功，添加日志
        PayLog payLog = new PayLog();
        payLog.setNote("支付\"" + order.getId() + "\"订单成功！");
        payLog.setCreateTime(now);
        payLog.setCreateUserId(userId);
        payLog.setAccountId(accountId);
        payLog.setSrcAmount(account.getBalance());
        payLog.setRemainingAmount(remainingAmount);
        payLog.setDeductionAmount("-" + needPay.toString());
        payLog.insert();
        //更新支付accontId
        order.setStatus(OrderStatus.PAY.getValue());
        updateOrder(order);
        return AjaxResult.success();
    }

    @Override
    public  void updateOrder(Order order) {
        OrderTypeService orderTypeService = OrderTypeFactory.getService(order.getType());
        //不同订单类型更新可能不一样
        orderTypeService.updateOrder(order);
    }

    @Override
    public void backOrder(Integer orderState,Order order) {
        //修改订单
        // 加库存
        OrderTypeService orderService = OrderTypeFactory.getService(order.getType());
        order.setStatus(orderState);
        orderService.updateOrder(order);
        goodsService.addSeckillCount(order.getGoodsId(),order.getOrderDetail().getGoodsCount());

    }

    @Override
    @Transactional
    public AjaxResult deleteAllOrder(Order order) {
        if(!OrderStatus.isCanDelete(order.getStatus())) {
          return AjaxResult.error("订单状态不允许删除！");
        }
        OrderTypeService orderTypeService = OrderTypeFactory.getService(order.getType());
        //不同的订单删除订单的方式可能不同
        orderTypeService.deleteOrderOnly(order);
        order.getOrderDetail().deleteById();
        return AjaxResult.success();
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public AjaxResult returnOrder(Order order) {
//
//        Account account = accountService.getById(order.getAccountId());
//        //添加金额
//        BigDecimal needPay =  order.getOrderDetail().getGoodsPrice().multiply(new BigDecimal(order.getOrderDetail().getGoodsCount() + ""));
//
//        BigDecimal remainingAmount = accountService.addAmount(account.getId(), needPay);
//        if (remainingAmount == null) {
//            log.error("回退订单 修改金额失败！order=>{}",order);
//            return AjaxResult.error(RespBeanEnum.RETURN_ERROR.getMessage());
//        }
//        //表示操作成功，添加日志
//        PayLog payLog = new PayLog();
//        payLog.setNote("\"" + order.getId() + "\"订单回退成功！");
//        payLog.setCreateTime(LocalDateTime.now());
//        payLog.setCreateUserId(order.getUserId());
//        payLog.setAccountId(account.getId());
//        payLog.setSrcAmount(account.getBalance());
//        payLog.setRemainingAmount(remainingAmount);
//        payLog.setDeductionAmount("+" + needPay.toString());
//        payLog.insert();
//        //回退订单
//        backOrder(OrderStatus.RETURN.getValue(),order);
//        return AjaxResult.success();
//    }

    @Override
    @Transactional
    public AjaxResult cancleOrder(Order order) {
        //回退订单会删除缓存
        backOrder(OrderStatus.CANCLE.getValue(),order);
        return AjaxResult.success();
    }
}
