package com.example.seckilldemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckilldemo.dao.UserMapper;
import com.example.seckilldemo.entity.po.User;
import com.example.seckilldemo.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public void updateLoginTime(Long id) {
        User user = new User();
        user.setId(id);
        user.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(user);
    }
}
