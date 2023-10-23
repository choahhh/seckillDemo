//package com.example.seckilldemo.service.impl;
//
//
//import com.example.seckilldemo.dao.UserMapper;
//import com.example.seckilldemo.entity.po.User;
//import com.example.seckilldemo.entity.vo.FormUserDetails;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    @Autowired
//    UserMapper userMapper;
//
//    @Override
//    public UserDetails loadUserByUsername(String username)  {
//        FormUserDetails userDetails = new FormUserDetails();
//        User userPO = userMapper.findByName(username);
//        if (userPO == null) {
//            throw new BadCredentialsException("用户名不存在");
//        }
//
////        if (userPO.getIsDisable() == 1) {
////            throw new DisabledException("账号已禁用");
////        }
////        String incrementKey = String.format(userNameAutoKey, username);
////        String increment = redisTemplate.boundValueOps(incrementKey).get(0,-1);
////        if(StringUtils.hasText(increment) && Long.parseLong(increment) > 4L){
////            throw new DisabledException("错误登录次数过多，账号临时禁用半小时");
////        }
//
//        userDetails.setId(userPO.getId());
//        userDetails.setPassword(userPO.getPassword());
//        userDetails.setUserName(userPO.getUserName());
//        List<GrantedAuthority> auths = new ArrayList<>();
//
//        userDetails.setAuthorities(auths);
//        return userDetails;
//    }
//
//}