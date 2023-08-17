package com.cheney.seckill.exception;

import com.cheney.seckill.common.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义全局异常】
 * 我们抛出的所有异常都是该异常
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalException extends RuntimeException{
    private RespBeanEnum respBeanEnum;
}
