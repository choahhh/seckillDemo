package com.example.seckilldemo.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.seckilldemo.data.RedisKey;
import com.example.seckilldemo.data.RespBeanEnum;
import com.example.seckilldemo.entity.enums.OrderStatus;
import com.example.seckilldemo.entity.enums.OrderType;
import com.example.seckilldemo.entity.po.Goods;
import com.example.seckilldemo.entity.po.Order;
import com.example.seckilldemo.entity.vo.FormUserDetails;
import com.example.seckilldemo.order.OrderTypeService;
import com.example.seckilldemo.order.factory.OrderTypeFactory;
import com.example.seckilldemo.service.GoodsService;
import com.example.seckilldemo.service.OrderService;
import com.example.seckilldemo.service.SeckillService;
import com.example.seckilldemo.util.AjaxResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/order")
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


    @RequestMapping("/findAllOrderType")
    public AjaxResult findAllOrderType(){
        return AjaxResult.data(OrderType.findAllType());
    }

    @RequestMapping("/placeOrder")
    @ResponseBody
    public AjaxResult placeOrder(Long goodId, String type, Integer goodscount,FormUserDetails user) {
        if (user == null) {
            return AjaxResult.error(RespBeanEnum.SESSION_NOT_EXIT.getMessage());
        }
        if(!OrderType.isExist(type)) {
            return  AjaxResult.error("商品类型错误");
        }

        OrderTypeService orderTypeService = OrderTypeFactory.getService(type);
        String limitKey = RedisKey.LIMIT;
        //接口优化3：限流 - 利用计数器 5秒之内只能下50单
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Integer count = (Integer) valueOperations.get(limitKey);
        if (count==null){
            valueOperations.set(limitKey,1,5, TimeUnit.SECONDS);
        }else if (count < 50){
            valueOperations.increment(limitKey);
        }else{
            return AjaxResult.error(RespBeanEnum.ACCESS_LIMIT_REACHED.getMessage());
        }

        Goods goods = goodsService.getById(goodId);
        if(goods == null) {
            return AjaxResult.error(RespBeanEnum.GOOD_NOT_EXISTS.getMessage());
        }
        if(!orderTypeService.isPlace(goods)) {
            return AjaxResult.error(RespBeanEnum.NOT_IN_TIME.getMessage());
        }
//        if(goods.getStockCount() < goodscount) {
//            return AjaxResult.error(RespBeanEnum.EMPTY_STOCK.getMessage());
//        }
        return seckillService.seckillOrder(type,goodId, goodscount,user);
    }

    @RequestMapping("/payOrder")
    @ApiOperation("支付订单")
    public AjaxResult payOrder(Long orderId, Long accountId, FormUserDetails user) {
        //改造 多种订单用factory
        AjaxResult result = null;
        if (user == null) {
            return AjaxResult.error(RespBeanEnum.SESSION_NOT_EXIT.getMessage());
        }
        Order order = orderService.selectOrderInfo(orderId,user.getId());
        if (order == null) {
            return AjaxResult.error(RespBeanEnum.ORDER_NOT_EXIT.getMessage());
        }
        if(!OrderStatus.isCanPay(order.getStatus())) {
            return AjaxResult.error(RespBeanEnum.PAY_FAIL.getMessage());
        }
        try {
            result = orderService.payOrder(order, accountId, user.getId());
        } catch (Exception e) {
            log.error("支付订单出现异常！", e);
            result = AjaxResult.error(RespBeanEnum.ERROR.getMessage());
            //可以加个统计 存储历史总库存 然后进行对比 看是否有漏加库存
        }
        if (!result.isSuccess()) {
            //如果支付失败 回退订单
            log.error("开始回退订单order =》{}", order);
            orderService.backOrder(OrderStatus.PAY_FAIL.getValue(), order);
        }
        return result;
    }

    @RequestMapping("/cancle")
    @ApiOperation("取消订单")
    public AjaxResult cancleOrder(Long orderId, FormUserDetails user) {
        //改造 多种订单用factory
        if (user == null) {
            return AjaxResult.error(RespBeanEnum.SESSION_NOT_EXIT.getMessage());
        }
        Order order = orderService.selectOrderInfo(orderId,user.getId());
        if (order == null) {
            return AjaxResult.error(RespBeanEnum.ORDER_NOT_EXIT.getMessage());
        }
        if(!OrderStatus.isCanCancel(order.getStatus())) {
            return AjaxResult.error(RespBeanEnum.CANCLE_ERROE.getMessage());
        }
        return orderService.cancleOrder(order);
    }

    /**
     * 只能删除 失败的订单
     *
     * @param orderId
     * @param user
     * @return
     */
    @RequestMapping("/delOrder")
    @ApiOperation("删除订单")
    public AjaxResult delOrder(Long orderId, FormUserDetails user) {
        if (user == null) {
            return AjaxResult.error(RespBeanEnum.SESSION_NOT_EXIT.getMessage());
        }
        Order order = orderService.selectOrderInfo(orderId,user.getId());
        if (order == null) {
            return AjaxResult.error(RespBeanEnum.ORDER_NOT_EXIT.getMessage());
        }
        return orderService.deleteAllOrder(order);
    }


    //发货  由店家操作 所以要传入订单的创建用户即买家
    @ApiOperation("发货")
    @RequestMapping("shipped")
    public AjaxResult shipped(Long orderId,FormUserDetails user) {
        if (user == null) {
            return AjaxResult.error(RespBeanEnum.SESSION_NOT_EXIT.getMessage());
        }
        Order order = orderService.selectOrderInfo(orderId,user.getId());
        if (order == null) {
            return AjaxResult.error(RespBeanEnum.ORDER_NOT_EXIT.getMessage());
        }
        if (!OrderStatus.isCanShipped(order.getStatus())) {
            return AjaxResult.error(RespBeanEnum.SHIPPED_ERROR.getMessage());
        }
        order.setUpdateDate(LocalDateTime.now());
        order.setStatus(OrderStatus.SHIPPED.getValue());
        orderService.updateOrder(order);
        return AjaxResult.success();
    }

    @ApiOperation("收货")
    @RequestMapping("receipt")
    public AjaxResult receipt(Long orderId,FormUserDetails user) {
        if (user == null) {
            return AjaxResult.error(RespBeanEnum.SESSION_NOT_EXIT.getMessage());
        }
        Order order = orderService.selectOrderInfo(orderId,user.getId());
        if (order == null) {
            return AjaxResult.error(RespBeanEnum.ORDER_NOT_EXIT.getMessage());
        }
        if (!OrderStatus.isCanReceipt(order.getStatus())) {
            return AjaxResult.error(RespBeanEnum.RECEIPT_ERROR.getMessage());
        }
        order.setUpdateDate(LocalDateTime.now());
        order.setStatus(OrderStatus.RECEIPT.getValue());
        orderService.updateOrder(order);
        return AjaxResult.success();
    }

    /**
     * 只能退支付成功的订单
     */
    @ApiOperation("订单退货")
    @RequestMapping("returnGoods")
    public AjaxResult retuenGoods(Long orderId,FormUserDetails user){
        if (user == null) {
            return AjaxResult.error(RespBeanEnum.SESSION_NOT_EXIT.getMessage());
        }
        Order order = orderService.selectOrderInfo(orderId,user.getId());
        if (orderId == null) {
            return AjaxResult.error(RespBeanEnum.ORDER_NOT_EXIT.getMessage());
        }
        //退货只能退 支付成功之后的订单
        if (!OrderStatus.isCanReturn(order.getStatus())) {
            return AjaxResult.error(RespBeanEnum.RETURN_ERROR.getMessage());
        }
        //还要回退订单
        return orderService.returnOrder(order);
    }

    @ApiOperation("完成订单")
    @RequestMapping("complete")
    public AjaxResult complete(Long orderId,FormUserDetails user) {
        if (user == null) {
            return AjaxResult.error(RespBeanEnum.SESSION_NOT_EXIT.getMessage());
        }
        Order order = orderService.selectOrderInfo(orderId,user.getId());
        if (order == null) {
            return AjaxResult.error(RespBeanEnum.ORDER_NOT_EXIT.getMessage());
        }
        if (!OrderStatus.isCanComplete(order.getStatus())) {
            return AjaxResult.error(RespBeanEnum.COMPLETE_ERROR.getMessage());
        }
        order.setUpdateDate(LocalDateTime.now());
        order.setStatus(OrderStatus.COMPLETE.getValue());
        orderService.updateOrder(order);
        return AjaxResult.success();
    }


    @RequestMapping("/findGoodsInfo/{orderId}")
    @ApiOperation("查询商品明细")
    public AjaxResult findGoodDetail(@PathVariable Long orderId, FormUserDetails user) {
        Order order =  orderService.selectOrderInfo(orderId,user.getId());
        return AjaxResult.data(order);
    }


    @RequestMapping("/findAllOrder")
    @ApiOperation("查询该用户下所有的订单")
    public AjaxResult findGoodDetail(Page<Order> page, FormUserDetails user) {
        page = orderService.page(page,new LambdaQueryWrapper<Order>().eq(Order::getUserId, user.getId()));
        return AjaxResult.data(page);
    }

}
