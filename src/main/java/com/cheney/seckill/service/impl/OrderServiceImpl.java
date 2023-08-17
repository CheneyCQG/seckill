package com.cheney.seckill.service.impl;

import com.cheney.seckill.pojo.Order;
import com.cheney.seckill.mapper.OrderMapper;
import com.cheney.seckill.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author zhyp
 * @since 2023-08-10
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

}
