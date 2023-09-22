package com.example.seckilldemo.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_account")
@ApiModel(value = "用户账号实体类")
public class Account extends Model<Account> implements Serializable {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField
    @ApiModelProperty("余额")
    private BigDecimal balance = BigDecimal.ZERO;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("创建时间")
    @TableField(updateStrategy = FieldStrategy.NEVER)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime updateTime;

    @ApiModelProperty("创建用户Id")
    private Long createUserId;

}
