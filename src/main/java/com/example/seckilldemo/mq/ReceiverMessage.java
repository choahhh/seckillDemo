package com.example.seckilldemo.mq;

import com.example.seckilldemo.data.OrderQueue;
import com.example.seckilldemo.data.RedisKey;
import com.example.seckilldemo.entity.dto.GoodsDto;
import com.example.seckilldemo.entity.vo.OrderMessage;
import com.example.seckilldemo.order.OrderTypeService;
import com.example.seckilldemo.order.factory.OrderTypeFactory;
import com.example.seckilldemo.service.GoodsService;
import com.example.seckilldemo.service.SeckillService;
import com.example.seckilldemo.service.UserService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class ReceiverMessage {

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private UserService userService;

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
                        message = OrderQueue.getInstance().getMessage();
                    } catch (InterruptedException e) {
                        log.error("获取订单队列失败!");
                    }
                    log.info("接收到的消息：{}", message);
                    GoodsDto goodsDto = message.getGoodsDto();
                    Integer goodsCount = message.getGoodsCount();
                    Long user = message.getUserId();
                    OrderTypeService orderTypeService = OrderTypeFactory.getService(message.getType());
                    BigDecimal balance = message.getBalance();
                    try {
                        //下单 这个方法允许报错 因为会先执行redis库存的操作
                        seckillService.secKill(user, goodsDto, goodsCount, orderTypeService, balance);
                    } catch (Exception e) {
                        log.error("保存订单失败！", e);
                        goodsService.updateCountByRedis(goodsDto.getId(), goodsCount);
                        userService.updateBalanceByRedis(user, balance);                        //防止关闭线程
                    }
                    finally {
                        orderTypeService.releaseOrderOnlyLock(goodsDto.getId(),user);
                    }
                }
            });
        }
    }



//    @PostConstruct
    public void init() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/balance.lua")));
        redisScript.setResultType(Long.class);
        List<String> param = Lists.newArrayList();
        param.add(RedisKey.getAdvanceBalance(3l));
        Object execute = redisTemplate.execute(redisScript, param, 2.5);
        System.out.println(execute);
    }

}
