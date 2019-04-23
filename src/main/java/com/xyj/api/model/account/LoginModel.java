package com.xyj.api.model.account;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Accessors(chain = true)
@Data
public class LoginModel {
    private String nickname;
    private String openid;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("token值")
    private String token;
    @ApiModelProperty("账号")
    private String account;
    @ApiModelProperty("头像")
    private String headImg;
    @ApiModelProperty("会员等级")
    private String grade;
    @ApiModelProperty("是否有设置密码")
    private boolean notPassword;
    @ApiModelProperty("失效时间")
    private Date failureTime;
    @ApiModelProperty("会员id")
    private int memberId;
}
