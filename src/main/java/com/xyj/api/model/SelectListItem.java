package com.xyj.api.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SelectListItem {
    @ApiModelProperty(value = "文本")
    private String text;
    @ApiModelProperty(value = "值")
    private String value;
}
