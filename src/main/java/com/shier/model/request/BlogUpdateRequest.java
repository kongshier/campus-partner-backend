package com.shier.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 博客更新请求
 *
 * @author Shier
 * @date 2023/06/22
 */
@Data
@ApiModel(value = "更新博文请求")
public class BlogUpdateRequest implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = -669161052567797556L;
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;
    /**
     * img str
     */
    @ApiModelProperty(value = "已上传的图片")
    private String imgStr;
    /**
     * 图片
     */
    @ApiModelProperty(value = "未上传的图片")
    private MultipartFile[] images;
    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;
    /**
     * 内容
     */
    @ApiModelProperty(value = "正文")
    private String content;
}
