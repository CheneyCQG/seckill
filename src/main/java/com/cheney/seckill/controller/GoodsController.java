package com.cheney.seckill.controller;


import com.cheney.seckill.common.RespBean;
import com.cheney.seckill.common.RespBeanEnum;
import com.cheney.seckill.exception.GlobalException;
import com.cheney.seckill.pojo.SeckillGoods;
import com.cheney.seckill.pojo.User;
import com.cheney.seckill.service.ISeckillGoodsService;
import com.cheney.seckill.utils.CookieUtil;
import com.cheney.seckill.utils.JsonUtil;
import com.cheney.seckill.vo.DetailVo;
import com.cheney.seckill.vo.SeckillGoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.ServletRegistration;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author zhyp
 * @since 2023-08-10
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private ISeckillGoodsService iSeckillGoodsService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;
//    @RequestMapping("/toList")
//    public String toList(User user, Model model, HttpServletRequest request){
//        //1判断用户是否登录
//        if (user == null)
//            return "login";
//        model.addAttribute("user",user);
//        //2查询所有秒杀商品信息
//        List<SeckillGoodsVo> list = iSeckillGoodsService.tolist();
//        model.addAttribute("goodsVoList",list);
//        return "goodsList";
//    }
    @RequestMapping("/toList")
    @ResponseBody
    public String toList2(User user, Model model, HttpServletRequest request, HttpServletResponse response){
        //1.判断用户是否登录
        if (user == null)
            throw new GlobalException(RespBeanEnum.LOGIN_NOT_LOGIN);
        //2.从redis中取出页面缓存
        String html = (String) redisTemplate.opsForValue().get("goodsList");
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        model.addAttribute("user",user);
        //2查询所有秒杀商品信息
        List<SeckillGoodsVo> list = iSeckillGoodsService.tolist();
        model.addAttribute("goodsVoList",list);
        //ViewResolver把数据糅合到一起
        //ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
        //需要一个web的上下文
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        //需要WebContext参数
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList",webContext);
        //3.保存到redis
        redisTemplate.opsForValue().set("goodsList",html,60, TimeUnit.SECONDS);
        return html;
    }
//    @RequestMapping("/detail")
//    public String detail(User user,Long id, Model model, HttpServletRequest request) {
//        //1判断用户是否登录
//        if (user == null)
//            return "login";
//        model.addAttribute("user",user);
////        System.out.println(id);
//        SeckillGoodsVo goodsVo = iSeckillGoodsService.findGoodsVoById(id);
//        model.addAttribute("goods",goodsVo);
//
//        //添加两个参数
//        //当前的秒杀状态
//        Date startDate = goodsVo.getStartDate();
//        Date endDate = goodsVo.getEndDate();
//        Date nowDate = new Date();
//        int secKillStatus = -1;
//        int remainSeconds = -1;
//        if (nowDate.before(startDate))
//            secKillStatus = 0;
//        else if (nowDate.after(endDate))
//            secKillStatus = 2;//已经结束
//        else {
//            secKillStatus = 1;
//            remainSeconds = 0;
//        }
//        //如果未开始显示 剩余时间
//
//        if (secKillStatus == 0){
//            remainSeconds = (int)((startDate.getTime() - nowDate.getTime())/1000);
//        }
//        model.addAttribute("secKillStatus",secKillStatus);
//        model.addAttribute("remainSeconds",remainSeconds);
//        return "goodsDetail";
//    }

//    @RequestMapping("/detail")
//    @ResponseBody
//    public String detail2(User user,Long id, Model model, HttpServletRequest request,HttpServletResponse response) {
//        //1判断用户是否登录
//        if (user == null)
//            throw new GlobalException(RespBeanEnum.LOGIN_NOT_LOGIN);
//        //2从redis中获取url缓存
//        String html = (String) redisTemplate.opsForValue().get("goodsDetail:uid:" + id);
//        if (!StringUtils.isEmpty(html)) {
//            return html;
//        }
//        //3查出来，放到model中
//        model.addAttribute("user",user);
////        System.out.println(id);
//        SeckillGoodsVo goodsVo = iSeckillGoodsService.findGoodsVoById(id);
//        model.addAttribute("goods",goodsVo);
//
//        //添加两个参数
//        //当前的秒杀状态
//        Date startDate = goodsVo.getStartDate();
//        Date endDate = goodsVo.getEndDate();
//        Date nowDate = new Date();
//        int secKillStatus = -1;
//        int remainSeconds = -1;
//        if (nowDate.before(startDate))
//            secKillStatus = 0;
//        else if (nowDate.after(endDate))
//            secKillStatus = 2;//已经结束
//        else {
//            secKillStatus = 1;
//            remainSeconds = 0;
//        }
//        //如果未开始显示 剩余时间
//
//        if (secKillStatus == 0){
//            remainSeconds = (int)((startDate.getTime() - nowDate.getTime())/1000);
//        }
//        model.addAttribute("secKillStatus",secKillStatus);
//        model.addAttribute("remainSeconds",remainSeconds);
//
//        //4使用Thymeleaf模板引擎处理
//        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap()));
//        redisTemplate.opsForValue().set("goodsDetail:uid"+id,html,10,TimeUnit.SECONDS);
//        return html;
//    }

    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public RespBean detail3(User user, @PathVariable("goodsId") Long id, Model model, HttpServletRequest request) {
        //1判断用户是否登录
        if (user == null)
            throw new GlobalException(RespBeanEnum.LOGIN_NOT_LOGIN);

//        System.out.println(id);
        SeckillGoodsVo goodsVo = iSeckillGoodsService.findGoodsVoById(id);

        //添加两个参数
        //当前的秒杀状态
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int secKillStatus = -1;
        int remainSeconds = -1;
        if (nowDate.before(startDate))
            secKillStatus = 0;
        else if (nowDate.after(endDate))
            secKillStatus = 2;//已经结束
        else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        //如果未开始显示 剩余时间

        if (secKillStatus == 0){
            remainSeconds = (int)((startDate.getTime() - nowDate.getTime())/1000);
        }
        DetailVo detailVo = new DetailVo(user, goodsVo, secKillStatus, remainSeconds);
        return RespBean.success(detailVo);
    }
}
