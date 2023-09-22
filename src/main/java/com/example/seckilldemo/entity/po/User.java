package com.example.seckilldemo.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author 作者
 * @since 2022-03-25
 */
@TableName("t_user")
@Data
@ApiModel(value = "User对象", description = "")
public class User extends Model<User> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField
    private String userName;

    private String password;



    @TableField
    private LocalDateTime lastLoginDate;

    private String telephone;

    @TableField
    private LocalDateTime createTime;
    @TableField
    private LocalDateTime updateTime;



}
