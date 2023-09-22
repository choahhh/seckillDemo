package com.example.seckilldemo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckilldemo.entity.po.Goods;
import com.example.seckilldemo.entity.po.GoodsDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    int addSeckillCount(@Param("goodsId") Long goodsId, @Param("seckillCount") Integer seckillCount);

    int subtractSeckillCount(@Param("goodsId")Long goodsId, @Param("goodsCount")Integer goodsCount);

    Integer selectLockSeckillCount(@Param("goodsId") Long goodsId);

    GoodsDetail selectGoodInfo(@Param("goodsId") Long goodsId);

    Goods findGoodDetail(@Param("goodsId") Long goodsId,@Param("userId") Long userId);
}
