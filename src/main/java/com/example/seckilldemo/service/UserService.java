package com.example.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckilldemo.entity.po.User;

public interface UserService extends IService<User> {
    void updateLoginTime(Long id);
}
