package com.xyj.api.model.account;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class EmployeeView {
    @ApiModelProperty("工号(登录账号)")
    private String No;
    @ApiModelProperty("密码(默认123456)")
    private String Password;
}
