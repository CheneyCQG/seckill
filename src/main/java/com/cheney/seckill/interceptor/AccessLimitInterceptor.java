package com.cheney.seckill.interceptor;

import com.cheney.seckill.annotation.AccessLimit;
import com.cheney.seckill.common.RespBean;
import com.cheney.seckill.common.RespBeanEnum;
import com.cheney.seckill.exception.GlobalException;
import com.cheney.seckill.pojo.User;
import com.cheney.seckill.threadlocal.UserHolder;
import com.cheney.seckill.utils.CookieUtil;
import com.cheney.seckill.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Component
public class AccessLimitInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前被拦截的对视是不是springmvc的COntroller层的某个方法
        if (handler instanceof HandlerMethod) {
            //强转成HandlerMethod
            HandlerMethod method = (HandlerMethod) handler;
            //判断是不是有AccessLimit注解
            if (method.hasMethodAnnotation(AccessLimit.class)) {
                //获取注解
                AccessLimit accessLimit = method.getMethodAnnotation(AccessLimit.class);
                //获取注解属性
                int time = accessLimit.time();
                int count = accessLimit.count();
                User user = null;
                boolean needLogin = accessLimit.needLogin();
                //根据属性判断
                if (needLogin) {
                    //用户是否登录
                    String uuid = CookieUtil.getCookieValue(request, "token");
                    if (StringUtils.isEmpty(uuid)) {
                        responseToHTML(response, RespBeanEnum.LOGIN_NOT_LOGIN);
                        return false;
                    }
//        Object users = request.getSession().getAttribute(uuid);
//        if (users == null)
//            return "login";
                    String userJson = (String) redisTemplate.opsForValue().get("user:uuid:" + uuid);
                    if (userJson == null) {
                        responseToHTML(response, RespBeanEnum.LOGIN_NOT_LOGIN);
                        return false;
                    }

                    user = JsonUtil.jsonStr2Object(userJson, User.class);
                    UserHolder.setUser(user);
                }
                //限流
                String url = request.getRequestURL().toString();
                Integer realcount = (Integer) redisTemplate.opsForValue().get("seckill:count:" + url + ":" + user.getId());
                if (realcount == null) {
                    redisTemplate.opsForValue().set("seckill:count:" + url + ":" + user.getId(), 1, 20, TimeUnit.SECONDS);
                } else {
                    realcount++;
                    if (realcount > count) {
                        responseToHTML(response, RespBeanEnum.LIMIT_OUTOFBOUNDS_ERROR);

                        return false;
                    }
                    redisTemplate.opsForValue().increment("seckill:count:" + url + ":" + user.getId());

                }
            }
        }
        return true;
    }

    private void responseToHTML(HttpServletResponse response, RespBeanEnum respBeanEnum) {
        //响应头
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        //响应数据
        try {
            RespBean respBean = RespBean.error(respBeanEnum);
            String respStr = JsonUtil.object2JsonStr(respBean);
            PrintWriter outputStream = response.getWriter();
            outputStream.write(respStr);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new GlobalException(RespBeanEnum.ERROR);
        }
    }
}
