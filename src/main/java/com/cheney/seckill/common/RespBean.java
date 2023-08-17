package com.cheney.seckill.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespBean {
    private long code;
    private String msg;
    private Object data;

    public RespBean(Object data) {
        this(200,null,data);
    }

    public RespBean(String msg) {
        this(200,msg,null);
    }

    /**
     * 统一的成功返回数据的对象
     * @param data
     * @return
     */
    public static RespBean success(Object data){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(),RespBeanEnum.SUCCESS.getMsg(), data);
    }

    /**
     * 统一的成功返回信息的对象
     * @param msg
     * @return
     */
    public static RespBean success(String msg){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(),msg,null);
    }

    /**
     * 统一的成功返回的对象
     *
     * @return
     */
    public static RespBean success(){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(),RespBeanEnum.SUCCESS.getMsg(), null);
    }

    /**
     * 统一的失败返回对象
     * @return
     */
    public static RespBean error(RespBeanEnum beanEnum){
        return new RespBean(beanEnum.getCode(),beanEnum.getMsg(),null);
    }


}
