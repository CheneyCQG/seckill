package com.cheney.seckill.service;

import com.cheney.seckill.pojo.SeckillGoods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cheney.seckill.vo.SeckillGoodsVo;

import java.util.List;

/**
 * <p>
 * 秒杀商品表 服务类
 * </p>
 *
 * @author zhyp
 * @since 2023-08-10
 */
public interface ISeckillGoodsService extends IService<SeckillGoods> {

    List<SeckillGoodsVo> tolist();

    SeckillGoodsVo findGoodsVoById(Long id);
}
