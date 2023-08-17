package com.cheney.seckill.mapper;

import com.cheney.seckill.pojo.SeckillGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cheney.seckill.vo.SeckillGoodsVo;

import java.util.List;

/**
 * <p>
 * 秒杀商品表 Mapper 接口
 * </p>
 *
 * @author zhyp
 * @since 2023-08-10
 */
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

    List<SeckillGoodsVo> getListInfo();

    SeckillGoodsVo findGoodsVoById(Long id);
}
