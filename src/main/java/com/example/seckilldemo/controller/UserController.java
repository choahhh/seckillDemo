package com.example.seckilldemo.controller;


import com.example.seckilldemo.entity.po.User;
import com.example.seckilldemo.service.UserService;
import com.example.seckilldemo.util.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/seckill/user")
@Api(tags = "用户接口")
public class UserController {

    @Autowired
    private UserService userService;


    @Autowired
    RedisTemplate redisTemplate;


//    @RequestMapping(value = "/edit")
//    @ApiOperation("修改用户")
//    public AjaxResult doEdit(User user) {
//        log.info("编辑用户...user={}", user.toString());
//        user.setUpdateTime(LocalDateTime.now());
//        userService.updateById(user);
//        return AjaxResult.success();
//    }

    @PostMapping("/delete/{id}")
    @ApiOperation("删除用户")
    @ApiImplicitParam(value = "用户id", name = "id", paramType = "query", dataType = "String")
    public boolean delete(@PathVariable String id) {
        return userService.removeById(id);
    }


    @PostMapping("/addAmount")
    @ApiOperation("删除用户")
    @ApiImplicitParam(value = "用户id", name = "id", paramType = "query", dataType = "String")
    public AjaxResult addAmount(Long userId, BigDecimal balance) {
        User user = new User();
        user.setId(userId);
        user.setBalance(balance);
        user.updateById();
        userService.syncBalance(userId);
        return AjaxResult.success();
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
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult<Object> register(@RequestBody User userVO) throws Exception {
        userService.saveUser(userVO);

        return AjaxResult.success();
    }

}
