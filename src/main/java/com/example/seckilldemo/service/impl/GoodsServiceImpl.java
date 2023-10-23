package com.example.seckilldemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckilldemo.dao.GoodsMapper;
import com.example.seckilldemo.data.EmptyStockMap;
import com.example.seckilldemo.data.RedisKey;
import com.example.seckilldemo.entity.po.Goods;
import com.example.seckilldemo.service.GoodsService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@CacheConfig(cacheNames = {"good"})
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    @Transactional
    @CacheEvict(key = "#p0.id")
    public void saveGoods(Goods goods) {
        baseMapper.insert(goods);
        String seckillCount = RedisKey.getAdvanceCount(goods.getId());
        //先存入redis
        redisTemplate.opsForValue().set(seckillCount,goods.getStock().intValue());
    }

    @Override
    @Cacheable(key = "#p0", unless = "#result == null")
    public Goods findGoodsNoCountById(Long goodsId) {
        Goods goods = baseMapper.selectById(goodsId);
        goods.setStock(null);
        return goods;
    }

    @Override
    public Boolean updateCountByRedis(Long goodsId, int i) {
        try {

            String advanceCount = RedisKey.getAdvanceCount(goodsId);
            Long increment = redisTemplate.opsForValue().increment(advanceCount, i);
            if (increment < 0) {
                redisTemplate.opsForValue().increment(advanceCount, (0 - i));
//                redisSync.syncCount(goodsId);
                log.error("redis 数量不足!可执行同步处理!");
                return false;
            }
        }catch (Exception e) {
            syncCount(goodsId);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Integer findCountByDb(Long goodsId) {

        return baseMapper.findCountByDb(goodsId);
    }

    @Override
    public Integer findCountByRedis(Long goodsId) {
        String advanceBalance = RedisKey.getAdvanceCount(goodsId);
        //先存入redis
        return (Integer) redisTemplate.opsForValue().get(advanceBalance);
    }





//    @Override
//    @Transactional
//    //数据库加锁 所以方法不需要加锁 保证原子
//    public AjaxResult advanceCalSeckill(GoodsDto goodsDto, Long userId, Integer goodsCount) {
//        Integer count = baseMapper.selectLockSeckillCount(goodsDto.getId());
//        BigDecimal balance = userService.findBalanceById(userId);
//        if (count  == null || count < goodsCount) {
//            return AjaxResult.error(RespBeanEnum.EMPTY_STOCK.getMessage());
//        }
//        if(goodsDto.getGoodsPrice().compareTo(balance) > 0 ) {
//            return AjaxResult.error(RespBeanEnum.NO_MONEY.getMessage());
//        }
//
//        //判断有没有出现 redis比库存高可能 被手动减少库存，需要删除redis 可能会导致 下不了单
//        //先查数据库 再查 redis 减少下面判断触发次数 只有 人工减少库存 会触发 顶多导致 后续下单减少库存失败
//        Integer curCount = (Integer) redisTemplate.opsForValue().get(RedisKey.getAdvanceCount(goodsDto.getId()));
//        Double price = (Double) redisTemplate.opsForValue().get(RedisKey.getAdvanceBalance(userId));
//
//        if(curCount != null && curCount > count) {
//            log.error("可能手动减库存 导致缓存数量比数据库高 清除redis。goosId=》{}",goodsDto.getId());
//            redisTemplate.delete(RedisKey.getAdvanceCount(goodsDto.getId()));
//        }
//
//        if(price != null && new BigDecimal(price.toString()).compareTo(balance) > 0) {
//            log.error("可能手动降余额 导致缓存数量比数据库高 清除redis。userId=》{}",userId);
//            redisTemplate.delete(RedisKey.getAdvanceBalance(userId));
//        }
//
//
//        //后面订单生成的时候需要加回去,redis的值一直在变化所以要获取最新值
//        Long increment = redisTemplate.opsForValue().increment(RedisKey.getAdvanceCount(goodsDto.getId()), goodsCount);
//
//        BigDecimal multiply = goodsDto.getGoodsPrice().multiply(new BigDecimal(goodsCount + ""));
//        Double incrementDouble = redisTemplate.opsForValue().increment(RedisKey.getAdvanceBalance(userId),multiply.doubleValue() );
//
//        AjaxResult result = AjaxResult.success();
//
//
//        if(incrementDouble > balance.doubleValue()) {
//            result = AjaxResult.error(RespBeanEnum.NO_MONEY.getMessage());
//        }
//        if(count - increment < 0) {
//            result = AjaxResult.error(RespBeanEnum.EMPTY_STOCK.getMessage());
//        }
//        if(!result.isSuccess()) {
//            redisTemplate.opsForValue().decrement(RedisKey.getAdvanceCount(goodsDto.getId()),goodsCount);
//            redisTemplate.opsForValue().increment(RedisKey.getAdvanceBalance(userId),0 - multiply.doubleValue());
//        }
//        return result;
//    }


//    @Overridee
//    public boolean advanceBalance(Goods goods, Integer count) {
//        return false;
//    }





    @Transactional(rollbackFor = Exception.class)
    @Override
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
        int i = baseMapper.subtractSeckillCount(goodsId,goodsCount);
        //减库存失败
        if (i <= 0) {
            return false;
        }
        return true;
    }

    @Override
//    @Cacheable(key = "#p0", unless = "#result == null")
    public Goods findById(Long goodsId) {
        return baseMapper.selectById(goodsId);
    }

    @Override
    @CacheEvict(key = "#p0.id")
    public void updateByid(Goods goods) {
         baseMapper.updateById(goods);
    }


    @Override
    public void syncCount(Long goodId) {
        List<Goods> list = Lists.newArrayList();
        if(goodId == null) {
            list = list();
            for (Goods goods : list) {
                redisTemplate.opsForValue().set(RedisKey.getAdvanceCount(goods.getId()), goods.getStock());
            }
        } else {
            Integer countByDb = findCountByDb(goodId);
            if(countByDb != null) {
                redisTemplate.opsForValue().set(RedisKey.getAdvanceCount(goodId), countByDb);
            }
        }
    }
}
