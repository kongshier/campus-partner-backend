package com.shier.model.vo;

import com.shier.model.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 好友vo
 *
 * @author Shier
 * @date 2023/06/22
 */
@Data
@ApiModel(value = "好友记录返回")
public class FriendsRecordVO implements Serializable {
    private static final long serialVersionUID = 1928465648232335L;

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 申请状态 默认0 （0-未通过 1-已同意 2-已过期）
     */
    @ApiModelProperty(value = "申请状态")
    private Integer status;

    /**
     * 好友申请备注信息
     */
    @ApiModelProperty(value = "好友申请备注信息")
    private String remark;

    /**
     * 申请用户
     */
    @ApiModelProperty(value = "申请用户")
    private User applyUser;
}
