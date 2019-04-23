package com.xyj.api.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "RegisterDto注册对象")
public class RegisterDto {
    @ApiModelProperty(value = "姓名", required = true)
    @NotBlank(message = "姓名必填")
    private String realName;

    @ApiModelProperty(value = "手机号", required = true)
    @NotBlank(message = "手机号必填")
    private String phone;

    @ApiModelProperty("介绍人")
    private String introducer;

    @ApiModelProperty(value = "住址", required = true)
    @NotBlank(message = "住址必填")
    private String address;

    @ApiModelProperty(value = "城市", required = true)
    @NotNull(message = "城市必填")
    private int cityId;

    @ApiModelProperty(value = "地区编号", required = true)
    @NotBlank(message = "地区编号必填")
    private String areaCode;

    @ApiModelProperty(value = "验证码(8325)", required = true)
    @NotBlank(message = "验证码必填")
    private String code;

    @ApiModelProperty(value = "注册类型 0：正常，1：保姆", required = true, notes = "0：正常，1：保姆")
    @Max(value = 1, message = "取值0：正常，1：保姆")
    @Min(value = 0, message = "取值0：正常，1：保姆")
    private Integer registerType;

    @ApiModelProperty(value = "保姆工作模式(保姆必填)")
    private String baomuWorkModel;
    @ApiModelProperty(value = "保姆工作能力(保姆必填)")
    private List<Integer> baomuAbility;
    @ApiModelProperty(value = "从业起始时间(保姆必填)")
    private LocalDateTime workingLifeDate;
}
