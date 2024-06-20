package com.shier.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户队伍关系
 *
 * @author Shier
 * @TableName user_team
 * @date 2023/07/28
 */
@TableName(value ="user_team")
@Data
@ApiModel(value = "用户队伍")
public class UserTeam implements Serializable {
    /**
     * id
     */
    @TableId
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 队伍id
     */
    @ApiModelProperty(value = "队伍id")
    private Long teamId;

    /**
     * 加入时间
     */
    @ApiModelProperty(value = "加入时间")
    private Date joinTime;

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
    @ApiModelProperty(value = "逻辑删除")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}