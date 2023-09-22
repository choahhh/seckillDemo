package com.example.seckilldemo.order.factory;

import com.example.seckilldemo.entity.enums.OrderType;
import com.example.seckilldemo.order.OrderTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class OrderTypeFactory {
    @Autowired
    ApplicationContext applicationContext;

    static Map<String, OrderTypeService> orderTypeMap = new HashMap<>();


    @PostConstruct
    public void init() {


        Map<String, OrderTypeService> stringOrderTypeServiceMap = applicationContext.getBeansOfType(OrderTypeService.class, true, true);
        for (Map.Entry<String, OrderTypeService> item : stringOrderTypeServiceMap.entrySet()) {
            orderTypeMap.put(item.getValue().getType(), item.getValue());
        }
//
        log.info("订单类型：{}", orderTypeMap);
    }

    public static OrderTypeService getService(String type) {
        if (orderTypeMap.containsKey(type)) {
            return orderTypeMap.get(type);
        }
        return null;
    }

    public static OrderTypeService getService(OrderType bankEnum) {
        if (orderTypeMap.containsKey(bankEnum.getValue())) {
            return orderTypeMap.get(bankEnum.getValue());
        }
        return null;
    }

}
