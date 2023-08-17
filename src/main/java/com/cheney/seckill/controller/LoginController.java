package com.cheney.seckill.controller;

import com.cheney.seckill.common.RespBean;
import com.cheney.seckill.service.IUserService;
import com.cheney.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private IUserService iUserService;
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }
    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) throws InterruptedException {
        RespBean respBean = iUserService.doLogin(loginVo,request,response);
        Thread.sleep(2000);
        return respBean;
    }
}