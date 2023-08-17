package com.cheney.seckill.mq.rabbitmq;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cheney.seckill.config.RabbitMQConfig;
import com.cheney.seckill.pojo.Order;
import com.cheney.seckill.pojo.SeckillGoods;
import com.cheney.seckill.pojo.SeckillOrder;
import com.cheney.seckill.pojo.User;
import com.cheney.seckill.service.IOrderService;
import com.cheney.seckill.service.ISeckillGoodsService;
import com.cheney.seckill.service.ISeckillOrderService;
import com.cheney.seckill.utils.JsonUtil;
import com.cheney.seckill.vo.MQvo;
import com.cheney.seckill.vo.SeckillGoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private ISeckillGoodsService iSeckillGoodsService;
    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private ISeckillOrderService iSeckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiver(String  msg){
        //1转对象
        MQvo mQvo = JsonUtil.jsonStr2Object(msg, MQvo.class);
        User user = mQvo.getUser();
        Long id = mQvo.getId();
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
            return;
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
        iSeckillOrderService.save(seckillOrder);
        //加入redis，保存抢购成功的用户信息
        redisTemplate.opsForValue().set("seckill:userid:"+user.getId()+":goodsId:"+id,"");

    }
}
