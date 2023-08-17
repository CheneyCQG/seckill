package com.cheney.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cheney.seckill.mapper.GoodsMapper;
import com.cheney.seckill.pojo.Goods;
import com.cheney.seckill.pojo.SeckillGoods;
import com.cheney.seckill.mapper.SeckillGoodsMapper;
import com.cheney.seckill.service.ISeckillGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheney.seckill.vo.SeckillGoodsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 秒杀商品表 服务实现类
 * </p>
 *
 * @author cheney
 * @since 2023-08-17
 */
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements ISeckillGoodsService {
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    /**
     * 查询秒杀商品的所有信息
     * @return
     */
    @Override
    public List<SeckillGoodsVo> tolist() {
        //第一种思路: 先查询所有的秒杀商品表  然后根据秒杀商品表中goods_id字段,查询商品表
//        Map<Long ,SeckillGoodsVo> seckillGoodsVos = new HashMap<>();
//        List<SeckillGoodsVo> seckillGoodsVos1 = new ArrayList<>();
//
        //
//        List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectList(null);
//        List<Long> gIdList = new ArrayList<>();
//        for (SeckillGoods seckillGood : seckillGoods) {
//            SeckillGoodsVo seckillGoodsVo = new SeckillGoodsVo();
//            gIdList.add(seckillGood.getGoodsId());
//            BeanUtils.copyProperties(seckillGood,seckillGoodsVo);
//            seckillGoodsVos.put(seckillGood.getGoodsId(),seckillGoodsVo);
//        }
//        QueryWrapper<Goods> goodsQueryWrapper = new QueryWrapper<>();
//        goodsQueryWrapper.in("id",gIdList);
//        List<Goods> goods = goodsMapper.selectList(goodsQueryWrapper);
//        for (Goods good : goods) {
//            SeckillGoodsVo seckillGoodsVo = seckillGoodsVos.get(good.getId());
//            BeanUtils.copyProperties(good,seckillGoodsVo);
//            seckillGoodsVos1.add(seckillGoodsVo);
//        }
        //第二种思路: 使用多表联查 以秒表商品表左表进行左连接查询,条件goods_id相等
        List<SeckillGoodsVo> seckillGoodsVos1 =  seckillGoodsMapper.getListInfo();
        //无论使用哪一种思路,最后要封装成->SeckillGoodsVo

        return seckillGoodsVos1;
    }

    @Override
    public SeckillGoodsVo findGoodsVoById(Long id) {
        SeckillGoodsVo seckillGoodsVos =  seckillGoodsMapper.findGoodsVoById(id);
        //无论使用哪一种思路,最后要封装成->SeckillGoodsVo

        return seckillGoodsVos;
    }
}
