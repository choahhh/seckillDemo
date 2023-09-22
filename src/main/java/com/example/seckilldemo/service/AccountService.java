package com.example.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckilldemo.entity.po.Account;

import java.math.BigDecimal;

public interface AccountService extends IService<Account> {
    BigDecimal subtractAmount(Long accountId,BigDecimal goodsPrice);

    BigDecimal addAmount(Long accountId,BigDecimal goodsPrice);

}
