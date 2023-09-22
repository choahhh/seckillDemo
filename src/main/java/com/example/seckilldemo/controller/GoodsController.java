package com.example.seckilldemo.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.seckilldemo.entity.enums.OrderType;
import com.example.seckilldemo.entity.po.Goods;
import com.example.seckilldemo.entity.po.GoodsDetail;
import com.example.seckilldemo.entity.vo.FormUserDetails;
import com.example.seckilldemo.service.GoodsDetailService;
import com.example.seckilldemo.service.GoodsService;
import com.example.seckilldemo.util.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
@Slf4j
@Api("商品接口")
public class GoodsController {

    @Autowired
    private GoodsDetailService goodsDetailService;

    @Autowired
    private GoodsService goodsService;

    @RequestMapping("saveGoodsDetail")
    @ApiOperation("保存商品信息")
    public AjaxResult saveGoodsDetail(GoodsDetail goodsDetail,FormUserDetails user) {
        goodsDetail.setUserId(user.getId());
        goodsDetailService.saveOrUpdate(goodsDetail);
        return AjaxResult.success();
    }

    @RequestMapping("delGoodsDetail/{goodsId}")
    @ApiOperation("删除商品信息")
    public AjaxResult delGoodDetails(@PathVariable  Long goodsId) {
        goodsDetailService.removeById(goodsId);
        //删除商品 这些商品的详细信息是goodsID的
        goodsService.remove(new LambdaQueryWrapper<Goods>().eq(Goods::getGoodsId,goodsId));
        return AjaxResult.success();
    }


    @RequestMapping("saveGoods")
    @ApiOperation("保存商品")
    public AjaxResult saveGoods(Goods goods, FormUserDetails user) {

        if(!OrderType.isExist(goods.getType())) {
            return  AjaxResult.error("商品类型错误");
        }

        Long goodsId = goods.getGoodsId();
        GoodsDetail goodsDetail = goodsDetailService.getOne(new LambdaQueryWrapper<GoodsDetail>()
                .eq(GoodsDetail::getId, goodsId)
                .eq(GoodsDetail::getUserId, user.getId()));
        if(goodsDetail == null ) {
            return AjaxResult.error("该商品找不到详细信息!");
        }
        if (goods.getId() != null) {
            goods.updateById();

        } else {
            Goods good = goodsService.getOne(new LambdaQueryWrapper<Goods>()
                    .eq(Goods::getGoodsId, goods.getGoodsId())
                    .eq(Goods::getUserId, user.getId())
                    .eq(Goods::getType,goods.getType()));
            if(good != null) {
                return AjaxResult.error("该类型商品已存在");
            }
            goods.setUserId(user.getId());
            goods.insert();
        }
        return AjaxResult.success();
    }


    @RequestMapping("delGoods/{goodsId}")
    @ApiOperation("删除商品")
    public AjaxResult delGoods(@PathVariable  Long goodsId) {
        goodsService.removeById(goodsId);
        return AjaxResult.success();
    }



    @RequestMapping("/findGoodsInfo/{goodsId}")
    @ApiOperation("查询商品明细")
    public AjaxResult findGoodDetail(@PathVariable Long goodsId, FormUserDetails user) {
        Goods goods =  goodsService.findGoodDetail(goodsId,user.getId());
        return AjaxResult.data(goods);
    }


    @RequestMapping("/findAllGoodsDetail")
    @ApiOperation("查询该用户下所有的商品信息")
    public AjaxResult findGoodDetail(Page<GoodsDetail> page, FormUserDetails user) {
        page = goodsDetailService.page(page,new LambdaQueryWrapper<GoodsDetail>().eq(GoodsDetail::getUserId, user.getId()));
        return AjaxResult.data(page);
    }


    @RequestMapping("/findGood")
    @ApiOperation("查询该用户下所有的商品")
    public AjaxResult findGood(Page<Goods> page, FormUserDetails user) {
        page = goodsService.page(page,new LambdaQueryWrapper<Goods>().eq(Goods::getUserId, user.getId()));
        return AjaxResult.data(page);
    }



}
