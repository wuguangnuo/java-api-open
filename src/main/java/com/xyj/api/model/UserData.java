package com.xyj.api.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserData {
    @ApiModelProperty(value = "员工ID")
    private int id;
    @ApiModelProperty(value = "员工账号")
    private String account;
}
