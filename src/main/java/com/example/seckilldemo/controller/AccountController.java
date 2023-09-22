package com.example.seckilldemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.seckilldemo.entity.po.Account;
import com.example.seckilldemo.entity.vo.FormUserDetails;
import com.example.seckilldemo.service.AccountService;
import com.example.seckilldemo.util.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/account")
@Slf4j
@Api("账户接口")
public class AccountController {


    @Autowired
    private AccountService accountService;


    @RequestMapping("/findAccount")
    @ApiOperation("查询该用户下所有的账户信息")
    public AjaxResult findAccountPage(Page<Account> page, FormUserDetails user) {
        page = accountService.page(page, new LambdaQueryWrapper<Account>().eq(Account::getCreateUserId, user.getId()));
        return AjaxResult.data(page);
    }


    @RequestMapping("/findAccount/{accountId}")
    @ApiOperation("查询该用户下所有的账户信息")
    public AjaxResult findAccountDetail(@PathVariable("accountId") Long accountId,FormUserDetails user) {
        Account account = accountService.getOne(new LambdaQueryWrapper<Account>().eq(Account::getId, accountId).eq(Account::getCreateUserId, user.getId()));
        if(account == null) {
            return AjaxResult.error("查不到账户！");
        }
        return AjaxResult.data(account);
    }

    @RequestMapping("/addAccount")
    @ApiOperation("新增账户")
    public AjaxResult addAcount(Account account, FormUserDetails user) {

        if (account.getId() != null) {
            account.updateById();
        } else {
            account.setCreateTime(LocalDateTime.now());
            account.setCreateUserId(user.getId());
            account.setUpdateTime(LocalDateTime.now());
            account.insert();
        }
        return AjaxResult.success();
    }

    @RequestMapping("/addMoney")
    @ApiOperation("存储金额")
    public AjaxResult addMoney(BigDecimal money, Long accountId, FormUserDetails user) {
        Account account = accountService.getOne(new LambdaQueryWrapper<Account>().eq(Account::getId, accountId).eq(Account::getCreateUserId, user.getId()));
        if (account == null) {
            return AjaxResult.error(user.getId() + "用户，没有" + accountId + "账号!");
        }
        if(money.compareTo(BigDecimal.ZERO) < 0) {
            return AjaxResult.error("存储金额只能是正数");
        }
        BigDecimal bigDecimal = accountService.addAmount(accountId, money);
        if(bigDecimal == null) {
            return AjaxResult.error("存储金额失败");
        }
        return AjaxResult.success();

    }


    @RequestMapping("/delAccount/{accountId}")
    @ApiOperation("删除账户信息")
    public AjaxResult delAccount(@PathVariable Long accountId, FormUserDetails user) {
        Account account = accountService.getOne(new LambdaQueryWrapper<Account>().eq(Account::getId, accountId).eq(Account::getCreateUserId, user.getId()));
        if (account == null) {
            return AjaxResult.error(user.getId() + "用户，没有" + accountId + "账号!");
        }
        account.deleteById();
        return AjaxResult.success();
    }


}
