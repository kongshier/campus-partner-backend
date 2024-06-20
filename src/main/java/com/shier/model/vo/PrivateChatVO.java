package com.shier.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 私聊返回
 *
 * @author shier
 * @date 2024/05/23
 */
@Data
@ApiModel(value = "私聊返回")
public class PrivateChatVO implements Serializable, Comparable<PrivateChatVO> {

    private static final long serialVersionUID = -3426382762617526337L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户Id")
    private Long userId;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String username;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String avatarUrl;

    /**
     * 最后一条消息
     */
    @ApiModelProperty(value = "最后消息")
    private String lastMessage;

    /**
     * 最后一条消息日期
     */
    @ApiModelProperty(value = "最后消息日期")
    private Date lastMessageDate;

    /**
     * 未读消息数量
     */
    @ApiModelProperty(value = "未读消息数量")
    private Integer unReadNum;


    @Override
    public int compareTo(PrivateChatVO other) {
        return -this.getLastMessageDate().compareTo(other.getLastMessageDate());
    }
}
