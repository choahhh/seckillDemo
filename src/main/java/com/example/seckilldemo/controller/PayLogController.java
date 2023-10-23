package com.example.seckilldemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.seckilldemo.entity.po.PayLog;
import com.example.seckilldemo.service.PayLogService;
import com.example.seckilldemo.util.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payLog")
@Slf4j
@Api("支付日志接口")
public class PayLogController {

    @Autowired
    private PayLogService payLogService;
    @RequestMapping("/findAllLog")
    @ApiOperation("查询该用户下所有的日志信息")
    public AjaxResult findGoodDetail(Page<PayLog> page, Long user) {
        page = payLogService.page(page,new LambdaQueryWrapper<PayLog>().eq(PayLog::getCreateUserId, user).orderByDesc(PayLog::getCreateTime));
        return AjaxResult.data(page);
    }
}
