package com.xyj.api.dto.test;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 图片压缩DTO
 */
@ApiModel(value = "图片压缩DTO")
@Data
public class ThumbnailatorDto {
    @ApiModelProperty(value = "图片链接")
    private String imgUrl;
    @ApiModelProperty(value = "压缩率(0-1;默认0.5;1为不压缩)")
    @Size(max = 1, min = 0, message = "压缩率错误")
    private double scale;
}
