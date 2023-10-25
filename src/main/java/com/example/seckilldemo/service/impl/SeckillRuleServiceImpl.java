package com.example.seckilldemo.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckilldemo.dao.SeckillRuleMapper;
import com.example.seckilldemo.data.RedisKey;
import com.example.seckilldemo.entity.po.SeckillRule;
import com.example.seckilldemo.service.SeckillRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SeckillRuleServiceImpl extends ServiceImpl<SeckillRuleMapper, SeckillRule> implements SeckillRuleService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void setRule(SeckillRule seckillRule) {
        baseMapper.deleteById(1);
        seckillRule.setId(1L);
        seckillRule.insert();
        String jsonString = JSON.toJSONString(seckillRule);
        redisTemplate.opsForValue().set(RedisKey.getSeckillRule(),jsonString);
    }

    @Override
    public SeckillRule getRule() {
        Object o = redisTemplate.opsForValue().get(RedisKey.getSeckillRule());
        if(o == null) {
            return baseMapper.selectById(1);
        }
        return JSON.parseObject((String)o, SeckillRule.class);
    }
}
