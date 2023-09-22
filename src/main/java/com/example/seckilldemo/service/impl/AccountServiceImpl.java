package com.example.seckilldemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckilldemo.dao.AccountMapper;
import com.example.seckilldemo.entity.po.Account;
import com.example.seckilldemo.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    @Override
    @Transactional
    public BigDecimal subtractAmount(Long accountId,BigDecimal goodsPrice) {
        int i = baseMapper.subtractAmount(accountId, goodsPrice);
        if(i > 0) {
           return  baseMapper.selectById(accountId).getBalance();
        }
        return null;
    }

    @Override
    @Transactional
    public BigDecimal addAmount(Long accountId, BigDecimal goodsPrice) {
        int i = baseMapper.addAmount(accountId, goodsPrice);
        if(i > 0) {
            return  baseMapper.selectById(accountId).getBalance();
        }
        return null;
    }
}
