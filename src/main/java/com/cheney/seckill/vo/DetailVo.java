package com.cheney.seckill.vo;

import com.cheney.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailVo {
    private User user;
    private SeckillGoodsVo goodsVo;
    private int secKillStatus;
    private int remainSeconds;
}
