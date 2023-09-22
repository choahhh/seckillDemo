package com.example.seckilldemo.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel("支付日志")
@Data
@TableName("t_pay_log")
public class PayLog extends Model<PayLog> {

    @TableId(type =  IdType.AUTO)
    private  Long id;

    private Long accountId;

    @ApiModelProperty("原始金额")
    private BigDecimal srcAmount;

    @ApiModelProperty("扣款金额 -为扣款")
    private String deductionAmount;

    @ApiModelProperty("剩余金额")
    private BigDecimal remainingAmount;

    private LocalDateTime createTime;

    private String note;

    private Long createUserId;


}
