package com.xyj.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@Accessors(chain = true)
public class LocData {
    @ApiModelProperty(value = "经度")
    @Max(value = 180, message = "经度输入有误")
    @Min(value = -180, message = "经度输入有误")
    private double lon;

    @ApiModelProperty(value = "纬度")
    @Max(value = 90, message = "纬度输入有误")
    @Min(value = -90, message = "纬度输入有误")
    private double lat;
}
