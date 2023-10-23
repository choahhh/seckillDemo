package com.example.seckilldemo.service;

import com.example.seckilldemo.entity.dto.GoodsDto;
import com.example.seckilldemo.order.OrderTypeService;
import com.example.seckilldemo.util.AjaxResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface SeckillService {
    AjaxResult seckillOrder(OrderTypeService orderTypeService, GoodsDto goods, Integer count, Long userId);


    @Transactional
    void secKill(Long userId, GoodsDto goodsDto, Integer goodsCount, OrderTypeService orderTypeService,
                    BigDecimal balance) throws Exception;
}
