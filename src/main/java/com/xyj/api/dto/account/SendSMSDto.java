package com.xyj.api.dto.account;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 发短信
 */
@Data
public class SendSMSDto {

    @ApiModelProperty(value = "手机号(返回的短信验证码:8325)")
    @NotBlank(message = "手机号必填")
    private String tel;

    @ApiModelProperty(value = "短信类型 0:注册;1:登录;2:找回密码;3:绑定手机")
    @Max(value = 3, message = "范围0~3")
    @Min(value = 0, message = "范围0~3")
    private Integer smsType;

    @ApiModelProperty(value = "时间戳", hidden = true)
    private String random;
}
