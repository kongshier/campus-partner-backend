package com.shier.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户签证官
 *
 * @author Shier
 * @date 2023/06/22
 */
@Data
@ApiModel(value = "用户返回")
public class UserVO implements Serializable {
    private static final long serialVersionUID = 642307645491206784L;
    /**
     * id
     */
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
    @ApiModelProperty(value = "用户账号")
    private String userAccount;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String avatarUrl;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    private Integer gender;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 标签列表 json
     */
    @ApiModelProperty(value = "标签")
    private String tags;

    /**
     * 状态 0 - 正常
     */
    @ApiModelProperty(value = "状态")
    private Integer userStatus;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date updateTime;

    private String profile;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    @ApiModelProperty(value = "用户角色")
    private Integer userRole;

    /**
     * 是否关注
     */
    @ApiModelProperty(value = "是否关注")
    private Boolean isFollow;

}
