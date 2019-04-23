package com.xyj.api.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 地区DTO
 */
@Data
public class AreaDto {
    @ApiModelProperty(value = "地区ID")
    private int id;
    @ApiModelProperty(value = "地区名称")
    private String name;
    @ApiModelProperty(value = "地区编码")
    private String areaCode;
}
