package com.example.seckilldemo.controller;

import com.example.seckilldemo.entity.po.SeckillRule;
import com.example.seckilldemo.service.SeckillRuleService;
import com.example.seckilldemo.util.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/seckill/active")
@Slf4j
@Api("设置秒杀时间")
public class ActiveController {


    @Autowired
    private SeckillRuleService seckillRuleService;


    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping("addAndUpdate")
    @ApiOperation("保存商品信息")
    public AjaxResult addAndUpdate(SeckillRule seckillRule) {
        if(seckillRule.getStartTime() == null || seckillRule.getEndTime() == null ||  seckillRule.getSeckillPrice() == null) {
            return AjaxResult.error("参数有误！");
        }
        seckillRuleService.setRule(seckillRule);
        //删除redis缓存

        Set keys = redisTemplate.keys("seckill::selectOrder*");
        redisTemplate.delete(keys);
        return AjaxResult.success();
    }



}
