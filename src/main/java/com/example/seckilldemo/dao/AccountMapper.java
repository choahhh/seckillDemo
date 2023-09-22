package com.example.seckilldemo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckilldemo.entity.po.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
    int subtractAmount(@Param("accountId") Long accountId, @Param("goodsPrice") BigDecimal goodsPrice);

    int addAmount(@Param("accountId") Long accountId,@Param("goodsPrice") BigDecimal goodsPrice);
}
