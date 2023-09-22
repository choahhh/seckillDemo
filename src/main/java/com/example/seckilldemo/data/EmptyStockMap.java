package com.example.seckilldemo.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EmptyStockMap {


    public static final Map<Long,Boolean> emptyStockMap = new ConcurrentHashMap<Long,Boolean>();

    public static Boolean containsGoodId(Long goodsId) {
        if(goodsId == null) {
            return false;
        }
        Boolean aBoolean = emptyStockMap.get(goodsId);
        return aBoolean != null ? aBoolean : false;
    }

    public static void removeGoodId(Long goodsId) {
        emptyStockMap.remove(goodsId);
    }

    public static void addGoodId(Long goodsId) {
        emptyStockMap.put(goodsId,true);
    }

}
