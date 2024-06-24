package com.shier.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 好友
 *
 * @author Shier
 * @TableName friends
 * @date 2023/07/28
 */
@TableName(value ="friends")
@Data
@ApiModel(value = "好友")
public class Friends implements Serializable {
    /**
     * 好友申请id
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 发送申请的用户id
     */
    @ApiModelProperty(value = "发送申请的用户id")
    private Long fromId;

    /**
     * 接收申请的用户id
     */
    @ApiModelProperty(value = "接收申请的用户id")
    private Long receiveId;

    /**
     * 是否已读(0-未读 1-已读)
     */
    @ApiModelProperty(value = "是否已读")
    private Integer isRead;

    /**
     * 申请状态 默认0 （0-未通过 1-已同意 2-已过期 3-已撤销）
     */
    @ApiModelProperty(value = "申请状态")
    private Integer status;

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
    @ApiModelProperty(value = "是否删除")
    private Integer isDelete;

    /**
     * 好友申请备注信息
     */
    @ApiModelProperty(value = "好友申请备注信息")
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}