package com.example.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckilldemo.entity.po.User;

import java.math.BigDecimal;

public interface UserService extends IService<User> {
    void saveUser(User user);

    User findUserNoBanlanceById(Long userId);


    BigDecimal findBalanceByDb(Long userId);

    BigDecimal findBalanceByRedis(Long userId);


    Boolean updateBalanceByRedis(Long userId, BigDecimal balance);


    //更新金额 不清除用户信息，查询用户信息不看金额
    int updateBalanceByDb(Long userId, BigDecimal balance);

    //更新金额 不清除用户信息，查询用户信息不看金额
    int addBalanceByDb(Long userId, BigDecimal balance);

    void syncBalance(Long userId);
}
