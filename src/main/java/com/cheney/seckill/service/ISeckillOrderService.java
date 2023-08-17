package com.cheney.seckill.service;

import com.cheney.seckill.pojo.Order;
import com.cheney.seckill.pojo.SeckillOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cheney.seckill.pojo.User;

/**
 * <p>
 * 秒杀订单表 服务类
 * </p>
 *
 * @author zhyp
 * @since 2023-08-10
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {
    /**
     * 秒杀功能 下单
     * @param user
     * @param id
     * @return
     */
    Order createOrder(User user, Long id);
}
