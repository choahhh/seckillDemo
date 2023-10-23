package com.example.seckilldemo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckilldemo.entity.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    User findByName(String username);

    BigDecimal findBalanceById(Long userId);


    int updateBalanceByDb(@Param("userId") Long userId, @Param("balance") BigDecimal balance);

    int addBalanceByDb(@Param("userId") Long userId, @Param("balance") BigDecimal balance);

}
