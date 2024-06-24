package com.shier.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 *
 * @author Shier
 * @TableName user
 * @date 2023/07/28
 */
@TableName(value ="user")
@Data
@ApiModel(value = "用户")
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String username;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String userAccount;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String avatarUrl;
    /**
     * 个人简介
     */
    @ApiModelProperty(value = "个人简介")
    private String profile;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    private Integer gender;

    /**
     * 用户密码
     */
    @ApiModelProperty(value = "用户密码")
    private String password;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 用户状态，0为正常
     */
    @ApiModelProperty(value = "用户状态，0为正常")
    private Integer status;

    @ApiModelProperty(value = "好友id")
    private String friendIds;

    /**
     * 用户角色 0-普通用户,1-管理员
     */
    @ApiModelProperty(value = "用户角色 0-普通用户,1-管理员")
    private Integer role;

    /**
     * 标签列表
     */
    @ApiModelProperty(value = "标签列表")
    private String tags;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 是否删除
     */

    @TableLogic
    @ApiModelProperty(value = "逻辑删除")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}