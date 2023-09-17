package com.shier.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求
 *
 * @author Shier
 * @date 2023/06/22
 */
@Data
@ApiModel(value = "用户注册请求")
public class UserRegisterRequest implements Serializable {

    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 电话
     */
    @ApiModelProperty(value = "手机号")
    private String phone;

    // /**
    //  * 代码
    //  */
    // @ApiModelProperty(value = "验证码")
    // private String code;

    /**
     * 用户帐户
     */
    @ApiModelProperty(value = "用户账号")
    private String userAccount;

    /**
     * 用户密码
     */
    @ApiModelProperty(value = "用户密码")
    private String userPassword;

    /**
     * 检查密码
     */
    @ApiModelProperty(value = "校验密码")
    private String checkPassword;

}
