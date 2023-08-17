package com.cheney.seckill.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cheney.seckill.annotation.AccessLimit;
import com.cheney.seckill.common.RespBean;
import com.cheney.seckill.common.RespBeanEnum;
import com.cheney.seckill.exception.GlobalException;
import com.cheney.seckill.mq.rabbitmq.MQSender;
import com.cheney.seckill.pojo.Order;
import com.cheney.seckill.pojo.SeckillGoods;
import com.cheney.seckill.pojo.SeckillOrder;
import com.cheney.seckill.pojo.User;
import com.cheney.seckill.service.IOrderService;
import com.cheney.seckill.service.ISeckillGoodsService;
import com.cheney.seckill.service.ISeckillOrderService;
import com.cheney.seckill.utils.JsonUtil;
import com.cheney.seckill.utils.MD5Util;
import com.cheney.seckill.vo.MQvo;
import com.cheney.seckill.vo.OrderVo;
import com.cheney.seckill.vo.SeckillGoodsVo;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 秒杀商品表 前端控制器
 * </p>
 *
 * @author zhyp
 * @since 2023-08-10
 */
@Controller
@Slf4j
@RequestMapping("/seckill")
public class SeckillGoodsController implements InitializingBean {
    @Autowired
    private ISeckillGoodsService iSeckillGoodsService;
    @Autowired
    private ISeckillOrderService iSeckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private DefaultRedisScript script;

    private HashMap<Long,Boolean> emptyMap = new HashMap<>();

//    @RequestMapping("/doSeckill")
//    public String doSeckill(User user, Long id, Model model){
//        //判断登录
//        if (user == null)
//            return "login";
//        //2查询商品信息查看是否有库存
//        SeckillGoodsVo vo = iSeckillGoodsService.findGoodsVoById(id);
//        if (vo.getStockCount() <= 0) {
//            model.addAttribute("msg","商品库存不足");
//            return "seckillFail";
//        }
//        model.addAttribute("goods",vo);
//        //3查询该用户是否下单过该商品
//        QueryWrapper<SeckillOrder> objectQueryWrapper = new QueryWrapper<>();
//        objectQueryWrapper.eq("user_id",user.getId());
//        objectQueryWrapper.eq("goods_id",id);
//        int count = iSeckillOrderService.count(objectQueryWrapper);
//        if (count > 0){
//            model.addAttribute("msg","已经购买过该商品了");
//            return "seckillFail";
//        }
//        //4下单
//        Order order = iSeckillOrderService.createOrder(user,id);
//        model.addAttribute("order",order);
//
//        return "orderDetail";
//    }
    @RequestMapping("/{path}/doSeckill")
    @ResponseBody
    @AccessLimit(time=20,count=5,needLogin=true)
    public RespBean doSeckill2(User user, Long id,@PathVariable("path")String path){
        //判断登录
        if (user == null)
            RespBean.error(RespBeanEnum.LOGIN_NOT_LOGIN);
        //判断请求路径是否合法
        String  redisPath =(String) redisTemplate.opsForValue().get("seckill:getRealPath:userid:" + user.getId() + ":id:" + id);
        if (StringUtils.isEmpty(path)||StringUtils.isEmpty(redisPath))
            return RespBean.error(RespBeanEnum.GOODS_PATH_NOT_EXIST);
        if (!path.equals(redisPath))
            return RespBean.error(RespBeanEnum.GOODS_PATH_NOT_INVALID);
        //2查询商品信息查看是否有库存
        //未优化
//        SeckillGoodsVo vo = iSeckillGoodsService.findGoodsVoById(id);
//        if (vo.getStockCount() <= 0) {
//            return RespBean.error(RespBeanEnum.GOODS_STOCK_NOT_ENOUGH);
//        }
        //内存标记中指定商品是否为空
        Boolean isEmpty = emptyMap.get(id);
        if (isEmpty) {
            return RespBean.error(RespBeanEnum.GOODS_STOCK_NOT_ENOUGH);
        }
        //优化  从redis查询库存
        Integer stockCount = (Integer) redisTemplate.opsForValue().get("seckill:goodId:" + id);
        if (stockCount == null) {
            return RespBean.error(RespBeanEnum.GOODS_NOT_EXIST);
        }
        if (stockCount < 1) {
            emptyMap.put(id,true);
            return RespBean.error(RespBeanEnum.GOODS_STOCK_NOT_ENOUGH);
        }
        //3查询该用户是否下单过该商品
        //未优化
//        QueryWrapper<SeckillOrder> objectQueryWrapper = new QueryWrapper<>();
//        objectQueryWrapper.eq("user_id",user.getId());
//        objectQueryWrapper.eq("goods_id",id);
//        int count = iSeckillOrderService.count(objectQueryWrapper);
//        if (count > 0){
//            return RespBean.error(RespBeanEnum.GOODS_DUPLICATE_BUY);
//        }
        //优化为，从redis中获取是否抢购过该商品
        Boolean result = redisTemplate.hasKey("seckill:userid:" + user.getId() + ":goodsId:" + id);
        if (result) {
            return RespBean.error(RespBeanEnum.GOODS_DUPLICATE_BUY);
        }
        //4下单
        //未优化
//        Order order = iSeckillOrderService.createOrder(user,id);
//        if (order == null) {
//            return RespBean.error(RespBeanEnum.GOODS_STOCK_NOT_ENOUGH);
//        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //优化  使用MQ异步处理下单请求，先扣减库存

        //修改redis的库存信息
        //redisTemplate.opsForValue().decrement("seckill:goodId:"+id);
        Long execute = (Long) redisTemplate.execute(script, Collections.singletonList("seckill:goodId:" + id));
        if (execute == 0){
            return RespBean.error(RespBeanEnum.GOODS_STOCK_NOT_ENOUGH);
        }
        //发送下单信息到MQ中
        MQvo mQvo = new MQvo(user, id);

        mqSender.sendMQ(JsonUtil.object2JsonStr(mQvo));

        //删除秒杀随机地址
        redisTemplate.delete("seckill:getRealPath:userid:" + user.getId() + ":id:" + id);
        //返回0 代表成功
        return RespBean.success(0);
    }

    @RequestMapping("/order/{id}")
    @ResponseBody
    public RespBean findOrderDetailById(@PathVariable("id") Long id){
        //1查询订单信息
        Order order = orderService.getById(id);
        //2查询goodVo
        SeckillGoodsVo vo = iSeckillGoodsService.findGoodsVoById(order.getGoodsId());
        OrderVo orderVo = new OrderVo(order, vo);
        return RespBean.success(orderVo);
    }
    @RequestMapping("/findOrder/{id}")
    @ResponseBody
    public RespBean findOrder(User user,@PathVariable("id") Long id){
        log.error("查询订单是否已经下单"+id);
        if (user == null)
            return RespBean.error(RespBeanEnum.LOGIN_NOT_LOGIN);
        //查询指定用户指定商品的订单是否存在
        SeckillOrder oneOrder = iSeckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("goods_id", id).eq("user_id", user.getId()));
        if (oneOrder == null) {
            return RespBean.success(0);
        }
        return RespBean.success(oneOrder.getOrderId());
    }
    @RequestMapping("/getRealPath")
    @ResponseBody
    @AccessLimit(time=20,count=5,needLogin=true)
    public RespBean getRealPath(User user,Long id,String code,HttpServletRequest request){
        //是否登录
        if (user == null){
            return RespBean.error(RespBeanEnum.LOGIN_NOT_LOGIN);
        }
        //限流
//        String url = request.getRequestURL().toString();
//        Integer count =(Integer) redisTemplate.opsForValue().get("seckill:count:" + url + ":" + user.getId());
//        if (count == null) {
//            redisTemplate.opsForValue().set("seckill:count:" + url + ":" + user.getId(),1,20,TimeUnit.SECONDS);
//        }else {
//            count ++;
//            if (count>5){
//                return RespBean.error(RespBeanEnum.LIMIT_OUTOFBOUNDS_ERROR);
//            }
//            redisTemplate.opsForValue().increment("seckill:count:" + url + ":" + user.getId());
//
//        }

        //判断验证码
        String rediscode =(String) redisTemplate.opsForValue().get("seckill:code:userid:" + user.getId() + ":id:" + id);
        if ( StringUtils.isEmpty(code)) {
            return RespBean.error(RespBeanEnum.GOODS_VERTIFY_CODE_EMPTY);
        }
        if (StringUtils.isEmpty(rediscode))
            return RespBean.error(RespBeanEnum.GOODS_VERTIFY_CODE_TIMEOUT);
        if (!code.equals(rediscode))
            return RespBean.error(RespBeanEnum.GOODS_VERTIFY_CODE_ERROR);
        //验证通过删除验证码
        redisTemplate.delete("seckill:code:userid:" + user.getId() + ":id:" + id);
        //随机路径
        Object md5 = MD5Util.md5("" + user.getId() + System.currentTimeMillis() + id);
        //存到redis中
        redisTemplate.opsForValue().set("seckill:getRealPath:userid:"+user.getId()+":id:"+id,md5,60, TimeUnit.SECONDS);
        return RespBean.success(md5);
    }

    //获取验证码
    @RequestMapping("/code")
    public void verifyCode(User user, Long id, HttpServletRequest request,HttpServletResponse response) {
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 算术类型
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 48);
        captcha.setLen(3);  // 几位数运算，默认是两位
        captcha.getArithmeticString();  // 获取运算的公式：3+2=?
        captcha.text();  // 获取运算的结果：5
        // 验证码存入redis
        redisTemplate.opsForValue().set("seckill:code:userid:"+user.getId()+":id:"+id,captcha.text().toLowerCase());

        // 输出图片流
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new GlobalException(RespBeanEnum.GOODS_VERTIFY_CODE_FAILED);
        }
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        //读取mysql中的秒杀商品数，缓存到redis
        List<SeckillGoods> goods = iSeckillGoodsService.list();
        goods.forEach(good->{
            redisTemplate.opsForValue().set("seckill:goodId:"+good.getId(),good.getStockCount());
            emptyMap.put(good.getId(),false);
        });

    }
}
