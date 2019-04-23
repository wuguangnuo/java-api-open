package com.xyj.api.dto.test;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "图片DTO(测试)")
public class ImageDto {
    @ApiModelProperty(value = "图片base64(包含URI)")
    private String img;
}
