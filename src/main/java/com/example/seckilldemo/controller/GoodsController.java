package com.example.seckilldemo.controller;


import com.example.seckilldemo.entity.po.Goods;
import com.example.seckilldemo.service.GoodsService;
import com.example.seckilldemo.service.RedisSync;
import com.example.seckilldemo.util.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckill/goods")
@Slf4j
@Api("商品接口")
public class GoodsController {


    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisSync redisSync;

    @RequestMapping("add")
    @ApiOperation("保存商品信息")
    public AjaxResult saveGoods(Goods goods) {

        goods.setId(null);

        if(goods.getPrice() == null || goods.getStock() == null ) {
            return AjaxResult.error("参数不完整");
        }
        goodsService.saveGoods(goods);
        return AjaxResult.success();
    }

    @RequestMapping("update")
    @ApiOperation("编辑商品")
    public AjaxResult update(Goods goods) {
        if(goods.getId() == null) {
            return AjaxResult.error("请填写id，无法找到商品");
        }
        goodsService.updateByid(goods);
        goodsService.syncCount(goods.getId());

        return AjaxResult.success();
    }



    @RequestMapping("delGoods/{goodsId}")
    @ApiOperation("删除商品")
    public AjaxResult delGoods(@PathVariable  Long goodsId) {
        goodsService.removeById(goodsId);
        return AjaxResult.success();
    }








}
