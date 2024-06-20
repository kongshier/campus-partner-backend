package com.shier.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求
 *
 * @author Shier
 * @date 2023/06/22
 */
@Data
@ApiModel(value = "更新用户请求")
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = -7852848771257290370L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long id;
    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String username;

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

    @ApiModelProperty(value = "角色")
    private Integer role;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 标签列表
     */
    @ApiModelProperty(value = "标签列表")
    private String tags;

    @ApiModelProperty(value = "验证码")
    private String code;
}
