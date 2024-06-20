package com.shier.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天
 *
 * @author Shier
 * @TableName chat
 * @date 2023/07/28
 */
@TableName(value ="chat")
@Data
@ApiModel(value = "聊天")
public class Chat implements Serializable {
    /**
     * 聊天记录id
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 发送消息id
     */
    @ApiModelProperty(value = "发送消息id")
    private Long fromId;

    /**
     * 接收消息id
     */
    @ApiModelProperty(value = "接收消息id")
    private Long toId;

    /**
     * 正文
     */
    @ApiModelProperty(value = "正文")
    private String text;

    /**
     * 聊天类型 1-私聊 2-群聊
     */
    @ApiModelProperty(value = "聊天类型")
    private Integer chatType;

    /**
     * 是否已读 1-已读 2-未读
     */
    @ApiModelProperty(value = "是否已读")
    private Integer isRead;

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
     * 队伍id
     */
    @ApiModelProperty(value = "队伍id")
    private Long teamId;

    /**
     * 逻辑删除
     */
    @TableLogic
    @ApiModelProperty(value = "逻辑删除")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}