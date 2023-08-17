package com.cheney.seckill.config;

import com.cheney.seckill.pojo.User;
import com.cheney.seckill.threadlocal.UserHolder;
import com.cheney.seckill.utils.CookieUtil;
import com.cheney.seckill.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        User user = UserHolder.getUser();
        if (user == null){
            String uuid = CookieUtil.getCookieValue(request, "token");
            if (StringUtils.isEmpty(uuid)){
                return null;
            }
//        Object users = request.getSession().getAttribute(uuid);
//        if (users == null)
//            return "login";
            String userJson = (String) redisTemplate.opsForValue().get("user:uuid:" + uuid);
            if (userJson == null)
                return null;
            user = JsonUtil.jsonStr2Object(userJson, User.class);
        }


        return user;
    }
}
