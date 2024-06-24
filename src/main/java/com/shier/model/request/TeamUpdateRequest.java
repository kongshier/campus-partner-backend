package com.shier.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 团队更新请求
 *
 * @author Shier
 * @date 2023/06/22
 */
@Data
@ApiModel(value = "更新队伍请求")
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = -8958909531937031372L;
    /**
     * id
     */
    @ApiModelProperty(value = "队伍id")
    private Long id;

    /**
     * 队伍名称
     */
    @ApiModelProperty(value = "队伍名称")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 最大人数
     */
    @ApiModelProperty(value = "最大人数")
    private Integer maxNum;

    /**
     * 过期时间
     */
    @ApiModelProperty(value = "过期时间")
    private Date expireTime;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "队长id")
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 封面图片
     */
    @ApiModelProperty(value = "封面图片")
    private String coverImage;


    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
