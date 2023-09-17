package com.shier.exception;

import com.shier.common.ErrorCode;

/**
 * 业务异常
 *
 * @author Shier
 * @date 2023/06/22
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 4946461703861202476L;
    /**
     * 代码
     */
    private final int code;

    /**
     * 描述
     */
    private final String description;

    /**
     * 业务异常
     *
     * @param message     消息
     * @param code        代码
     * @param description 描述
     */
    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    /**
     * 业务异常
     *
     * @param errorCode 错误代码
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    /**
     * 业务异常
     *
     * @param errorCode   错误代码
     * @param description 描述
     */
    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    /**
     * 获取代码
     *
     * @return int
     */
    public int getCode() {
        return code;
    }

    /**
     * 得到描述
     *
     * @return {@link String}
     */
    public String getDescription() {
        return description;
    }

}
