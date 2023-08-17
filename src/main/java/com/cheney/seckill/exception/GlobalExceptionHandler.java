package com.cheney.seckill.exception;

import com.cheney.seckill.common.RespBean;
import com.cheney.seckill.common.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public RespBean handler(Exception e) throws Exception {
        //将异常转换为RespBean对象
        //异常有很多种。可能是自己的异常，可能是系统的等等
        if (e instanceof GlobalException){
            GlobalException ge = (GlobalException) e;
            return RespBean.error(ge.getRespBeanEnum());
        }else if (e instanceof BindException){
            BindException be = (BindException) e;
            String msg = be.getBindingResult().getAllErrors().get(0).getDefaultMessage();
            return new RespBean(500502L,msg,null);
        }else {
            e.printStackTrace();
            return RespBean.error(RespBeanEnum.ERROR);
        }
    }
}
