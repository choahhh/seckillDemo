package com.example.seckilldemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckilldemo.dao.UserMapper;
import com.example.seckilldemo.data.RedisKey;
import com.example.seckilldemo.entity.po.User;
import com.example.seckilldemo.service.UserService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@CacheConfig(cacheNames = {"user"})
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @CacheEvict(key = "#p0.id")
    @Transactional
    public void saveUser(User user) {
        user.setCreateTime(LocalDateTime.now());
        baseMapper.insert(user);
        String advanceBalance = RedisKey.getAdvanceBalance(user.getId());
        //先存入redis
        redisTemplate.opsForValue().set(advanceBalance,user.getBalance().doubleValue());
    }

    @Override
    @Cacheable(key = "#p0", unless = "#result == null")
    public User findUserNoBanlanceById(Long userId) {
        User user = baseMapper.selectById(userId);
        user.setBalance(null);
        return user;
    }


    @Override
    public BigDecimal findBalanceByDb(Long userId) {
        return baseMapper.findBalanceById(userId);
    }

    @Override
    public BigDecimal findBalanceByRedis(Long userId) {
        String advanceBalance = RedisKey.getAdvanceBalance(userId);
        //先存入redis
        Double balance = (Double) redisTemplate.opsForValue().get(advanceBalance);
        return BigDecimal.valueOf(balance);
    }

    @Override
    public Boolean updateBalanceByRedis(Long userId, BigDecimal balance) {

//
        try {
            String advanceBalance = RedisKey.getAdvanceBalance(userId);
            Double increment = redisTemplate.opsForValue().increment(advanceBalance, balance.doubleValue());
            if (increment < -0.001) {
                redisTemplate.opsForValue().increment(advanceBalance, balance.negate().doubleValue());
//                syncBalance(userId);
                log.error("redis 余额不足!可执行同步处理!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            syncBalance(userId);
            return  false;
        }
        return  true;
    }



    @Override
    //更新金额 不清除用户信息，查询用户信息不看金额
    public int updateBalanceByDb(Long userId,BigDecimal balance){
        return baseMapper.updateBalanceByDb(userId,balance);

    }

    @Override
    //更新金额 不清除用户信息，查询用户信息不看金额
    public int addBalanceByDb(Long userId, BigDecimal balance){
        return baseMapper.addBalanceByDb(userId,balance);

    }

    @Override
    public void syncBalance(Long userId) {
        List<User> list = Lists.newArrayList();
        if(userId == null) {
            list = list();
            for (User user : list) {
                redisTemplate.opsForValue().set(RedisKey.getAdvanceBalance(user.getId()), user.getBalance().doubleValue());
            }
        } else {
            BigDecimal balanceByDb = findBalanceByDb(userId);
            if(balanceByDb != null) {
                redisTemplate.opsForValue().set(RedisKey.getAdvanceBalance(userId), balanceByDb.doubleValue());
            }
        }




    }




}
