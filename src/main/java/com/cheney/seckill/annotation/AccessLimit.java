package com.cheney.seckill.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {
    int time();//时间
    int count();//计数
    boolean needLogin() default true;//默认需要登录


}
