package com.shier.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回类
 *
 * @author Shier
 * @date 2023/07/28
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 8153981949738243118L;
    /**
     * 代码
     */
    @ApiModelProperty(value = "状态码")
    private int code;

    /**
     * 数据
     */
    @ApiModelProperty(value = "数据")
    private T data;

    /**
     * 消息
     */
    @ApiModelProperty(value = "消息")
    private String message;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 返回
     *
     * @param code        代码
     * @param data        数据
     * @param message     消息
     * @param description 描述
     */
    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    /**
     * 返回
     *
     * @param code    代码
     * @param data    数据
     * @param message 消息
     */
    public BaseResponse(int code, T data, String message) {
        this(code, data, message, "");
    }

    /**
     * 返回
     *
     * @param code 代码
     * @param data 数据
     */
    public BaseResponse(int code, T data) {
        this(code, data, "", "");
    }

    /**
     * 返回
     *
     * @param errorCode 错误代码
     */
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }

}
