package com.cheney.seckill.service.impl;

import com.cheney.seckill.common.RespBean;
import com.cheney.seckill.common.RespBeanEnum;
import com.cheney.seckill.exception.GlobalException;
import com.cheney.seckill.exception.GlobalExceptionHandler;
import com.cheney.seckill.pojo.User;
import com.cheney.seckill.mapper.UserMapper;
import com.cheney.seckill.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheney.seckill.utils.*;
import com.cheney.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author zhyp
 * @since 2023-08-10
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    /**
     * 用户登录的业务逻辑
     * @param loginVo
     * @return
     */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        //1判断合法性
//        if (!ValidatorUtil.isMobile(loginVo.getMobile()) ) {
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }
//        if (loginVo.getPassword().length() != 32)
//            return RespBean.error(RespBeanEnum.PASSWORD_ERROR);

        //2根据手机号查询用户是否存在
        User user = userMapper.selectById(loginVo.getMobile());
        if (user == null)
            throw new GlobalException(RespBeanEnum.Login_USER_NOT_EXITS_ERROR);
            //return RespBean.error(RespBeanEnum.Login_USER_NOT_EXITS_ERROR);
        //3比较密码是否一致
        String formPass = loginVo.getPassword();
        String toDBPass = MD5Util.formPassToDBPass(formPass, user.getSalt());
        if (!toDBPass.equals(user.getPassword()))
            throw new GlobalException(RespBeanEnum.LOGINVO_ERROR);
//            return RespBean.error(RespBeanEnum.LOGINVO_ERROR);
        //4保存用户登录信息  通过cookie+session
//        String uuid = CookieUtil.getCookieValue(request, "user:id");
        String uuid = UUIDUtil.uuid();
        Cookie cookie = new Cookie("token", uuid);
        cookie.setPath("/");
//        cookie.setMaxAge(60);
        response.addCookie(cookie);

//        HttpSession session = request.getSession();
//        session.setAttribute(uuid,user);
        //用户数据保存到redis中
        String userJson = JsonUtil.object2JsonStr(user);
        redisTemplate.opsForValue().set("user:uuid:"+uuid,userJson);


        return RespBean.success("登录成功");
    }
}
