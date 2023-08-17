package com.cheney.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cheney.seckill.pojo.Order;
import com.cheney.seckill.pojo.SeckillGoods;
import com.cheney.seckill.pojo.SeckillOrder;
import com.cheney.seckill.mapper.SeckillOrderMapper;
import com.cheney.seckill.pojo.User;
import com.cheney.seckill.service.IOrderService;
import com.cheney.seckill.service.ISeckillGoodsService;
import com.cheney.seckill.service.ISeckillOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheney.seckill.vo.SeckillGoodsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 秒杀订单表 服务实现类
 * </p>
 *
 * @author zhyp
 * @since 2023-08-10
 */
@Service
@Transactional
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {
    @Autowired
    private ISeckillGoodsService iSeckillGoodsService;
    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Order createOrder(User user, Long id) {
        //1扣库存

//        SeckillGoods one = iSeckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("id", id));
        SeckillGoodsVo one = iSeckillGoodsService.findGoodsVoById(id);
        one.setStockCount(one.getStockCount()-1);
        SeckillGoods seckillGoods = new SeckillGoods();
        BeanUtils.copyProperties(one,seckillGoods);
//        iSeckillGoodsService.updateById(seckillGoods);
        boolean update = iSeckillGoodsService.update(
                new UpdateWrapper<SeckillGoods>()
                        .setSql("stock_count = stock_count -1")
                        .eq("id", id)
                        .gt("stock_count", 0));
        if (!update) {
            return null;
        }
        //2生成普通订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(id);
        order.setGoodsName(one.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(one.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        order.setPayDate(new Date());

        iOrderService.save(order);
        //3生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(id);
        save(seckillOrder);
        //4加入redis，保存抢购成功的用户信息
        redisTemplate.opsForValue().set("seckill:userid:"+user.getId()+":goodsId:"+id,order);
        //5修改redis的库存信息
        redisTemplate.opsForValue().decrement("seckill:goodId:"+id);
        return order;
    }
}
