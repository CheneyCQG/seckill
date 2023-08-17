package com.cheney.seckill.service.impl;

import com.cheney.seckill.pojo.Goods;
import com.cheney.seckill.mapper.GoodsMapper;
import com.cheney.seckill.service.IGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author zhyp
 * @since 2023-08-10
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

}
