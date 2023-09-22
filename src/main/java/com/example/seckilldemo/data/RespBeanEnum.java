package com.example.seckilldemo.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Getter
public enum RespBeanEnum {
    //通用
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务端错误"),
    //登录
    LOGIN_ERROR(500210,"用户名或密码不正确"),
    MOBILE_ERROR(500211,"手机号号码格式不正确"),
    BIND_ERROR(500212,"参数校验异常"),
    MOBILE_NOT_EXIT(500213,"手机号码不存在"),
    PASSWORD_UPDATE_FAIL(500214,"密码更新失败"),
    SESSION_NOT_EXIT(500215,"用户不存在"),
    //秒杀模块
    EMPTY_STOCK(500500,"库存不足"),
    REPEAT_ERROR(500501,"每人限购一个"),
    REQUEST_ILLEGAL(500502,"请求非法，请重新尝试"),
    CAPTCHA_ERROR(500503,"验证码错误，请重新输入"),
    NOT_IN_TIME(500505,"货物不在秒杀时间内"),
    GOOD_NOT_EXISTS(500506,"商品不存在"),
    ACCESS_LIMIT_REACHED(500504,"访问过于频繁，请稍后重试"),
    //订单模块 5003XX
    ORDER_NOT_EXIT(500300,"订单不存在"),
    ACCOUNT_NOT_EXIT(500301,"支付账号不存在"),
    PAY_FAIL(500600,"支付失败"),
    SHIPPED_ERROR(500701,"只能修改已支付订单"),
    RECEIPT_ERROR(500702,"只能修改已发货订单"),
    RETURN_ERROR(500702,"只能修改支付成功后的订单"),
    COMPLETE_ERROR(500703,"只能修改已收货的订单"),
    CANCLE_ERROE(500704,"只能取消待支付的订单")
    ;

    private final  int code;
    private final String message;

}
