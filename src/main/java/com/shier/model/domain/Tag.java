package com.shier.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 标签
 *
 * @author Shier
 * @TableName tag
 * @date 2023/05/15
 */
@TableName(value ="tag")
@Data
@ApiModel(value = "标签")
public class Tag implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 标签名称
     */
    @ApiModelProperty(value = "标签名称")
    private String tagName;

    /**
     * 上传该标签的用户id
     */
    @ApiModelProperty(value = "上传该标签的用户id")
    private Long userId;

    /**
     * 父标签id
     */
    @ApiModelProperty(value = "父标签id")
    private Long parentId;

    /**
     * 是否为父标签 0-不是父标签，1-父标签
     */
    @ApiModelProperty(value = "是否为父标签 0-不是父标签，1-父标签")
    private Integer isParent;

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

    /**
     * 串行版本uid
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}