package com.shier.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *
 * @author Shier
 * @date 2023/06/22
 */
@Data
@ApiModel(value = "删除请求")
public class DeleteRequest implements Serializable {

    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = -7428525903309954640L;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private long id;
}
