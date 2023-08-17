package com.cheney.seckill.threadlocal;

import com.cheney.seckill.pojo.User;

public class UserHolder {
    private static ThreadLocal<User> threadLocal = new ThreadLocal<>();
    public static User getUser(){
        return threadLocal.get();
    }
    public static void setUser(User user){
        threadLocal.set(user);
    }
}
