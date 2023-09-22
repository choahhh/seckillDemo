package com.example.seckilldemo.service;

import com.example.seckilldemo.entity.vo.FormUserDetails;
import com.example.seckilldemo.util.AjaxResult;
import org.springframework.transaction.annotation.Transactional;

public interface SeckillService {
    AjaxResult seckillOrder(String type,Long goodId,Integer count, FormUserDetails user);


    @Transactional
    void secKill(FormUserDetails user, Long goodsId,Integer goodsCount,String type);

}
