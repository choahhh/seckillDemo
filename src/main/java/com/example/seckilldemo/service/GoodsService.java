package com.example.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckilldemo.entity.po.Goods;
import org.springframework.transaction.annotation.Transactional;

public interface GoodsService extends IService<Goods> {

     void saveGoods(Goods goods);


     Goods findGoodsNoCountById(Long goodsId);

     Boolean updateCountByRedis(Long id, int i);


     Integer findCountByDb(Long goodsId);

     Integer findCountByRedis(Long goodsId);

//     Integer selectSeckillCount(Long goodsId);

     /**
      * 预先判断库存 加锁 redis预先添加库存 然后计算
      * @param goodsId
      * @return
      */
//     AjaxResult advanceCalSeckill(GoodsDto goodsDto, Long userId, Integer goodsCount);
//
//
//     @Transactional
//     //数据库加锁 所以方法不需要加锁 保证原子
//     boolean subtractAdvanceCount(Long goodsId,Integer goodsCount);


     @Transactional(rollbackFor = Exception.class)
     void addSeckillCount(Long goodsId, Integer seckillCount);

     @Transactional(rollbackFor = Exception.class)
     boolean subtractSeckillCount(Long goodsId,Integer goodsCount);

     Goods findById(Long goodsId);

     void updateByid(Goods goods);


    void syncCount(Long goodId);
}
