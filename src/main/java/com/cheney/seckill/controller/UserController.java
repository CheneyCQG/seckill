package com.cheney.seckill.controller;


import com.cheney.seckill.common.RespBean;
import com.cheney.seckill.common.RespBeanEnum;
import com.cheney.seckill.pojo.User;
import com.cheney.seckill.service.IUserService;
import com.cheney.seckill.utils.CookieUtil;
import com.cheney.seckill.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author zhyp
 * @since 2023-08-10
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
     @RequestMapping("/info")
     @ResponseBody
     public RespBean info(User user){
        return RespBean.success(user);
     }
     @RequestMapping("/password/{password}")
     @ResponseBody
     public RespBean password(User user, @PathVariable("password") String password, HttpServletRequest request){
         if (user == null)
             return RespBean.error(RespBeanEnum.LOGIN_NOT_LOGIN);
         //2修改用户密码
         User user1 = userService.getById(user.getId());
         if (user1 == null)
             return RespBean.error(RespBeanEnum.Login_USER_NOT_EXITS_ERROR);
         //3修改密码
         String salt = "20230815";
         user1.setPassword(MD5Util.inputPassToDBPass(password,salt));
         user1.setSalt(salt);
         //4更新数据库 再删缓存
         userService.updateById(user1);

         String uuid = CookieUtil.getCookieValue(request, "token");

         redisTemplate.delete("user:uuid:"+uuid);
         return RespBean.success("修改密码成功！！");



     }

}
