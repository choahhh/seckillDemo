package com.example.seckilldemo.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author 作者
 * @since 2022-03-28
 */
@TableName("t_goods")
@ApiModel(value = "SeckillGoods对象", description = "")
@Data
public class Goods extends Model<Goods> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("秒杀商品ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    @ApiModelProperty("商品价格")
    private BigDecimal price;


    @ApiModelProperty("库存数量")
    private Integer stock;



    @ApiModelProperty("商品名称")
    private String name;


    @TableField
    private Integer iskill;


}
