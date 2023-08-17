package com.cheney.seckill.controller;

import com.cheney.seckill.common.RespBean;
import com.cheney.seckill.common.RespBeanEnum;
import com.cheney.seckill.mq.rabbitmq.MQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class TestController {
    @Autowired
    private MQSender mqSender;
    @RequestMapping("test")
    @ResponseBody
    public RespBean test(){
        return RespBean.success("hello");
    }
    @RequestMapping("thymeleaf")
    public RespBean test1(Model model){
        model.addAttribute("name","jack");
        return RespBean.success(model);
    }
    @RequestMapping("/mq")
    @ResponseBody
    public void mq(String msg){
        mqSender.sendMQ(msg);
    }
}