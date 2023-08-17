package com.cheney.seckill.vo;

import com.cheney.seckill.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVo {
    private Order order;
    private SeckillGoodsVo seckillGoodsVo;
}
