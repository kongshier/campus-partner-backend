package com.shier.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍
 * @author Shier
 * @TableName team
 * @date 2023/05/15
 */
@TableName(value ="team")
@Data
@ApiModel(value = "队伍")
public class Team implements Serializable {
    /**
     * id
     */
    @TableId
    @ApiModelProperty(value = "id")
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
     * 封面图片
     */
    @ApiModelProperty(value = "封面图片")
    private String coverImage;

    /**
     * 最大人数
     */
    @ApiModelProperty(value = "最大人数")
    private Integer maxNum;

    /**
     * 过期时间
     */
    @ApiModelProperty(value = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",locale = "zh", timezone = "GMT+8")
    private Date expireTime;

    /**
     *队长id
     */
    @ApiModelProperty(value = "队长id")
    private Long userId;

    /**
     * 状态
     * 0 - 公开，1 - 私有，2 - 加密
     */
    @ApiModelProperty(value = "状态 0 - 公开，1 - 私有，2 - 加密")
    private Integer status;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",locale = "zh", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",locale = "zh", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @ApiModelProperty(value = "逻辑删除")
    private Integer isDelete;

    /**
     * 串行版本uid
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}