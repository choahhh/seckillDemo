package com.example.seckilldemo.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("秒杀商品ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    @ApiModelProperty("商品价格")
    private BigDecimal goodsPrice;



    @ApiModelProperty("商品名称")
    private String name;

    public GoodsDto(Long id, BigDecimal goodsPrice, String name) {
        this.id = id;
        this.goodsPrice = goodsPrice;
        this.name = name;
    }
}
