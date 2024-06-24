package com.shier.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

/**
 * 签到表
 *
 * @author Shier
 * @TableName sign
 */
@TableName(value = "sign")
@Data
public class Sign implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 签到的日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",locale = "zh", timezone = "GMT+8")
    private Timestamp signDate;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",locale = "zh", timezone = "GMT+8")
    private Date createTime;

    /**
     * 是否补签 0-不补签 1-补签
     */
    private Integer isBackup;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}