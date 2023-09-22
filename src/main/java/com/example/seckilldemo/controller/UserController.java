package com.example.seckilldemo.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.seckilldemo.entity.po.User;
import com.example.seckilldemo.service.UserService;
import com.example.seckilldemo.util.AjaxResult;
import com.example.seckilldemo.util.ValidateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/user1")
@Api(tags = "用户接口")
public class UserController {

    @Autowired
    private UserService userService;


    @RequestMapping("getCueUser")
    public Authentication getUser(){
      return   SecurityContextHolder.getContext().getAuthentication();
    }



    @RequestMapping(value = "/edit")
    @ApiOperation("修改用户")
    public AjaxResult doEdit(User user) {
        log.info("编辑用户...user={}", user.toString());
        user.setUpdateTime(LocalDateTime.now());
        userService.updateById(user);
        return AjaxResult.success();
    }

    @PostMapping("/delete/{id}")
    @ApiOperation("删除用户")
    @ApiImplicitParam(value = "用户id", name = "id", paramType = "query", dataType = "String")
    public boolean delete(@PathVariable String id) {
        return userService.removeById(id);
    }

//
//    @PostMapping("/detail/{id}")
//    @PreAuthorize("hasAuthority('user_del')")
//    @ApiOperation("用户详情")
//    @ApiImplicitParam(value = "用户id", name = "id", paramType = "query", dataType = "String")
//    public AjaxResult detail(@PathVariable String id) {
//        UserPO userPO = userService.getById(id);
//
//        List<UserRolePO> list = userRoleService.selectByUserId(userPO.getOwnerId());
//        userPO.setRoleIds(new ArrayList<>());
//        for (UserRolePO item : list) {
//            userPO.getRoleIds().add(item.getRoleId());
//        }
//        return AjaxResult.data(userPO);
//    }


    @ApiOperation("注册新用户")
    @PostMapping("/register")
    @ResponseBody
    public AjaxResult<Object> register(User userVO) throws Exception {
        String password = userVO.getPassword();
        userVO.setPassword(new BCryptPasswordEncoder().encode(password));

        String result = checkRegisterUser(userVO);
        if (!"1".equals(result)) {
            return AjaxResult.error(result);
        }
        return AjaxResult.success(String.valueOf(userService.save(userVO)));
    }
    private String checkRegisterUser(User userVO) {
        boolean flag;
        flag = ValidateUtil.checkRegex("^[0-9|A-Z|a-z]{6,20}$",
                userVO.getUserName());
        if (!flag) {
            return "用户名称只能用数字或者字母最短6位最长不超过20位";
        }
        //查询用户名是否注册
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUserName,userVO.getUserName()));
        if (null != user) {
            return "用户名已注册,不能重复注册";
        }
        return "1";
    }
}
