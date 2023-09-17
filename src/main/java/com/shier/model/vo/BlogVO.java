package com.shier.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.shier.model.domain.Blog;

import java.io.Serializable;

/**
 * 博客vo
 *
 * @author Shier
 * @date 2023/06/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "博文返回")
public class BlogVO extends Blog implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = -1461567317259590205L;
    /**
     * 就像
     */
    @ApiModelProperty(value = "是否点赞")
    private Boolean isLike;
    /**
     * 封面图片
     */
    @ApiModelProperty(value = "封面图片")
    private String coverImage;
    /**
     * 作者
     */
    @ApiModelProperty(value = "作者")
    private UserVO author;
}
