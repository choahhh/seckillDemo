package com.example.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckilldemo.entity.po.SeckillRule;

public interface SeckillRuleService  extends IService<SeckillRule> {
    void setRule(SeckillRule seckillRule);


    SeckillRule getRule();
}
