package com.shier.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 好友添加请求
 *
 * @author Shier
 * @date 2023/06/22
 */
@Data
@ApiModel(value = "好友添加请求")
public class FriendAddRequest implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = 1472823745422792988L;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;
    /**
     * 接收申请的用户id
     */
    @ApiModelProperty(value = "接收申请的用户id")
    private Long receiveId;

    /**
     * 好友申请备注信息
     */
    @ApiModelProperty(value = "好友申请备注信息")
    private String remark;
}
