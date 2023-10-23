package com.example.seckilldemo.entity.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.List;

@Getter
@AllArgsConstructor
public enum OrderType {
    SECKILL("秒杀订单", "seckill"),
    COMMON("普通订单", "common")

    ;

    private String name;

    private String value;

    public static boolean isExist(String type) {
        if(!StringUtils.hasText(type)) {
            return false;
        }
        OrderType[] values = OrderType.values();
        for(OrderType orderType:values) {
            if(orderType.getValue().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> findAllType() {
        List<String> orderTypeList = Lists.newArrayList();
        OrderType[] values = OrderType.values();
        for(OrderType orderType:values) {
            orderTypeList.add(orderType.getValue());
        }
        return orderTypeList;
    }
}
