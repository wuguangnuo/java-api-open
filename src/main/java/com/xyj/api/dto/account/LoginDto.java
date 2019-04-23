package com.xyj.api.dto.account;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginDto {
    @ApiModelProperty("密码")
    @NotBlank(message = "密码必填")
    private String password;

    @ApiModelProperty("账号")
    @NotBlank(message = "账号必填")
    private String account;
}
