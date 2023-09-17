package com.shier.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 更新密码请求
 *
 * @author Shier
 * @date 2023/06/22
 */
@Data
@ApiModel(value = "密码更新请求")
public class UpdatePasswordRequest {
    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    private String phone;
    // /**
    //  * 验证码
    //  */
    // @ApiModelProperty(value = "验证码")
    // private String code;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;
    /**
     * 确认密码
     */
    @ApiModelProperty(value = "确认密码")
    private String confirmPassword;
}
