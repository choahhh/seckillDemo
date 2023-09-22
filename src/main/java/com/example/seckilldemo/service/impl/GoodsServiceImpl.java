package com.example.seckilldemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckilldemo.dao.GoodsMapper;
import com.example.seckilldemo.data.EmptyStockMap;
import com.example.seckilldemo.data.RedisKey;
import com.example.seckilldemo.entity.po.Goods;
import com.example.seckilldemo.entity.po.GoodsDetail;
import com.example.seckilldemo.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Integer selectSeckillCount(Long goodsId) {
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Goods::getGoodsId, goodsId);
        Goods seckillGoods = getOne(queryWrapper);
        return seckillGoods == null ? 0 : seckillGoods.getStockCount();
    }

    @Override
    @Transactional
    //数据库加锁 所以方法不需要加锁 保证原子
    public boolean advanceCalSeckillCount(Long goodsId,Integer goodsCount) {
        //写一个service 来同步redis 实时查看库存
        Integer count = baseMapper.selectLockSeckillCount(goodsId);
        if (count  == null || count < goodsCount) {
            return false;
        }

        //判断有没有出现 redis比库存高可能 被手动减少库存，需要删除redis 可能会导致 下不了单
        //先查数据库 再查 redis 减少下面判断触发次数 只有 人工减少库存 会触发 顶多导致 后续下单减少库存失败
        Integer curCount = (Integer) redisTemplate.opsForValue().get(RedisKey.getAdvanceCount(goodsId));
        if(curCount != null && curCount > count) {
            log.error("可能手动减库存 导致缓存数量比数据库高 清楚redis。goosId=》{}",goodsId);
            redisTemplate.delete(RedisKey.getAdvanceCount(goodsId));
        }
        //后面订单生成的时候需要加回去,redis的值一直在变化所以要获取最新值
        Long increment = redisTemplate.opsForValue().increment(RedisKey.getAdvanceCount(goodsId), goodsCount);
        if(count - increment < 0) {
            //不够减 加回去
            redisTemplate.opsForValue().decrement(RedisKey.getAdvanceCount(goodsId),goodsCount);
            return false;
        }
        return true;
    }

    @Override
    public boolean subtractAdvanceCount(Long goodsId,Integer goodsCount) {
        try {
            //说明订单完成 预先 使用库存 减掉
            Integer count = (Integer) redisTemplate.opsForValue().get(RedisKey.getAdvanceCount(goodsId));
            if (count != null && count >= goodsCount) {
                redisTemplate.opsForValue().decrement(RedisKey.getAdvanceCount(goodsId),goodsCount);
            } else {
//                redisTemplate.opsForValue().set(RedisKey.getAdvanceCount(goodsId),0);
                //减失败说明redis被清空过了 出现的现象就是一直下不了单
                log.error("预先库存减少失败! goodsId=>{},goodsCount=>{}", goodsId ,goodsCount);
            }
        } catch (Exception e) {
            //报错直接清楚RedisKey.getAdvanceCount(goodsId)key ，不过会使得订单无法生成
            log.error("预先库存减少失败! goodsId=>{}", goodsId + "", e);
            return false;
        }
        return true;
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
//    @Caching(evict = {@CacheEvict(key = "'seckillCount'+#p0")})
    public void addSeckillCount(Long goodsId, Integer seckillCount) {
        try {
            baseMapper.addSeckillCount(goodsId, seckillCount);
            redisTemplate.delete("isStockEmpty:" + goodsId);
            EmptyStockMap.removeGoodId(goodsId);
        } catch (Exception e) {
            log.error("秒杀订单库存添加失败!{goodsId}", goodsId, e);
            throw  new RuntimeException("添加库存失败!");
        }
    }


    /**
     * 减库存操作数据库加锁 保证唯一 保证原子
     *
     * @param goodsId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean subtractSeckillCount(Long goodsId,Integer goodsCount) {
        boolean b = subtractAdvanceCount(goodsId,goodsCount);
        if(!b) {
            return b;
        }
        int i = baseMapper.subtractSeckillCount(goodsId,goodsCount);
        //减库存失败
        if (i <= 0) {
            return false;
        }
        Integer c = selectSeckillCount(goodsId);
        if (c <= 0) {
            redisTemplate.opsForValue().set("isStockEmpty:" + goodsId, "0");
        }
        return true;
    }

    /**
     * goodsId是秒杀货物的id
     *
     * @param goodsId
     * @return
     */
    @Override
    public GoodsDetail selectGoodInfo(Long goodsId) {

        return baseMapper.selectGoodInfo(goodsId);
    }

    @Override
    public Goods findGoodDetail(Long goodsId, Long userId) {
        return baseMapper.findGoodDetail(goodsId,userId);
    }
}
