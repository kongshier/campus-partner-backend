package com.shier.model.request;

import com.shier.common.PageRequest;
import com.shier.model.vo.UserVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 团队查询请求
 *
 * @author Shier
 * @date 2023/06/22
 */
@Data
@ApiModel(value = "队伍搜索请求")
public class TeamQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 9111600376030432964L;
    /**
     * id
     */
    @ApiModelProperty(value = "队伍id")
    private Long id;

    /**
     * id 列表
     */
    @ApiModelProperty(value = "id列表")
    private List<Long> idList;

    /**
     * 搜索关键词（同时对队伍名称和描述搜索）
     */
    @ApiModelProperty(value = "搜索关键词")
    private String searchText;

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
     * 最大人数
     */
    @ApiModelProperty(value = "最大人数")
    private Integer maxNum;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "队长id")
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    @ApiModelProperty(value = "状态0 - 公开，1 - 私有，2 - 加密")
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
     * 创建人用户信息
     */
    @ApiModelProperty(value = "创建人")
    private UserVO createUser;

    private String leaderName;
    /**
     * 已加入的用户数
     */
    @ApiModelProperty(value = "已加入的用户数")
    private Long hasJoinNum;

}
