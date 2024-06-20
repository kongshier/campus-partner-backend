package com.shier.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 关注
 *
 * @author Shier
 * @TableName follow
 * @date 2023/07/28
 */
@TableName(value ="follow")
@Data
@ApiModel(value = "关注")
public class Follow implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 关注的用户id
     */
    @ApiModelProperty(value = "关注的用户id")
    private Long followUserId;

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
     * 逻辑删除
     */
    @ApiModelProperty(value = "逻辑删除")
    private Integer isDelete;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}