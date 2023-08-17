package com.cheney.seckill.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum RespBeanEnum {
    //通用状态码
    SUCCESS(200L,"success"),
    ERROR(500L,"服务端异常"),
    //登录模块5002xx
    SESSION_ERROR(500210L,"session不存在或者已经失效"),
    LOGINVO_ERROR(500211L,"用户名或者密码错误"),
    MOBILE_ERROR(500212L,"手机号码格式错误"),
    PASSWORD_ERROR(500213L,"密码格式错误"),
    Login_USER_NOT_EXITS_ERROR(500214L,"手机号未注册"),
    LOGIN_NOT_LOGIN(500214L,"该用户未登录"),
    GOODS_STOCK_NOT_ENOUGH(500301L,"商品库存不足"),
    GOODS_NOT_EXIST(500301L,"商品ID不存在"),
    GOODS_PATH_NOT_EXIST(500303L,"地址不存在"),
    GOODS_PATH_NOT_INVALID(500304L,"非法秒杀请求"),
    GOODS_VERTIFY_CODE_FAILED(500305L,"验证码生成失败"),
    GOODS_VERTIFY_CODE_ERROR(500306L,"验证码错误"),
    GOODS_VERTIFY_CODE_EMPTY(500307L,"验证码不能为空"),
    GOODS_VERTIFY_CODE_TIMEOUT(500308L,"验证码失效"),
    LIMIT_OUTOFBOUNDS_ERROR(500401L,"您的访问过于频繁，限流了"),
    GOODS_DUPLICATE_BUY(500302L,"已经购买过该商品了");

    private long code;
    private String msg;

    public long getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

