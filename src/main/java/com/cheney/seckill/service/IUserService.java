package com.cheney.seckill.service;

import com.cheney.seckill.common.RespBean;
import com.cheney.seckill.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cheney.seckill.vo.LoginVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author zhyp
 * @since 2023-08-10
 */
public interface IUserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);
}
