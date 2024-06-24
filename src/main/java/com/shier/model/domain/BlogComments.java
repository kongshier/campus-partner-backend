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
 * 博客评论
 *
 * @author Shier
 * @TableName blog_comments
 * @date 2023/07/28
 */
@TableName(value ="blog_comments")
@Data
@ApiModel(value = "博文评论")
public class BlogComments implements Serializable {
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
     * 博文id
     */
    @ApiModelProperty(value = "博文id")
    private Long blogId;

    /**
     * 关联的1级评论id，如果是一级评论，则值为0
     */
    @ApiModelProperty(value = "关联的1级评论id")
    private Long parentId;

    /**
     * 回复的评论id
     */
    @ApiModelProperty(value = "回复的评论id")
    private Long answerId;

    /**
     * 回复的内容
     */
    @ApiModelProperty(value = "回复的内容")
    private String content;

    /**
     * 点赞数
     */
    @ApiModelProperty(value = "id")
    private Integer likedNum;

    /**
     * 状态，0：正常，1：被举报，2：禁止查看
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

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
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}