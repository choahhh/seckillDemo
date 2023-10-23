package com.example.seckilldemo.data;

public class RedisKey {
    public static final String ORDER_KEY = "seckill::selectOrder_%s_%s";
    public static final String ADVANCE_COUNT = "advance:count:%s";

    public  static final String ADVANCE_BALANCE = "advance:balance:%S";
    public static final String SECKILL_RULE = "seckill:rule";


    public static final String LIMIT = "limit";


    public static String getOrderKey(Long goodsId,Long userId) {
        return  String.format(ORDER_KEY,goodsId,userId);
    }

    public static String getAdvanceBalance(Long userId) {
        return  String.format(ADVANCE_BALANCE,userId);
    }

    public static String getAdvanceCount(Long goodsId) {
        return  String.format(ADVANCE_COUNT,goodsId);
    }

    public static String getSeckillRule() {
        return  SECKILL_RULE;
    }


}
