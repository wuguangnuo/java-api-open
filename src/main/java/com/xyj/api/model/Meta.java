package com.xyj.api.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Meta {
    @ApiModelProperty(value = "状态码")
    private int State;
    @ApiModelProperty(value = "返回信息")
    private String Msg;
}
