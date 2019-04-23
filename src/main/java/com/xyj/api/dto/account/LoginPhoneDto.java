package com.xyj.api.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginPhoneDto {
    @ApiModelProperty("验证码")
    @NotBlank(message = "验证码必填")
    private String phoneCode;

    @ApiModelProperty("手机号")
    @NotBlank(message = "手机号必填")
    private String phoneNumber;
}
