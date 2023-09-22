package com.example.seckilldemo.mq;

import com.example.seckilldemo.data.OrderQueue;
import com.example.seckilldemo.entity.po.Order;
import com.example.seckilldemo.entity.vo.FormUserDetails;
import com.example.seckilldemo.entity.vo.OrderMessage;
import com.example.seckilldemo.order.OrderTypeService;
import com.example.seckilldemo.order.factory.OrderTypeFactory;
import com.example.seckilldemo.service.GoodsService;
import com.example.seckilldemo.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class ReceiverMessage {

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SeckillService seckillService;
    @Autowired
    RedisTemplate redisTemplate;

    @PostConstruct
    public void consumerSeckillMessage() {
        for (int i = 0; i < 20; i++) {
            threadPoolTaskExecutor.execute(() -> {
                //优化 可能不止一种订单 还有可能其他订单 可以用工厂模式处理
                OrderMessage message = null;
                while (true) {
                    try {
                        message = OrderQueue.getMessage();
                    } catch (InterruptedException e) {
                        log.error("获取订单队列失败!");
                    }
                    log.info("接收到的消息：{}", message);
//                SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(meesage,SeckillMessage.class);
                    //减库存
                    try {

                        Long goodsId = message.getGoodId();
                        Integer goodsCount = message.getGoodsCount();
                        FormUserDetails user = message.getUser();
                        OrderTypeService orderTypeService = OrderTypeFactory.getService(message.getType());
                        //判断是否重复购买
                        Order order = orderTypeService.selectOrderOnly(goodsId, user.getId());
                        if (order != null) {
                            //减去预减库存
                            log.error("订单重复购买=>{}", order);
                            //移除预先减的库存  方法里有防止错误的
                            goodsService.subtractAdvanceCount(goodsId, goodsCount);
                            continue;
                        }
                        //下单 这个方法允许报错 因为会先执行redis库存的操作
                        seckillService.secKill(user, goodsId, goodsCount, message.getType());
                    } catch (Exception e) {
                        log.error("保存订单失败！", e);
                        //防止关闭线程
//                        goodsService.subtractAdvanceCount(goodsId,goodsCount);
                    }
                }
            });
        }
    }

}
