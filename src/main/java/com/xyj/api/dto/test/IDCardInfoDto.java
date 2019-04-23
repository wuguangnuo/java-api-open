package com.xyj.api.dto.test;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * IDCardInfo身份证信息封装类
 */
@Data
public class IDCardInfoDto {
    @ApiModelProperty(value = "证件姓名")
    private String name;
    @ApiModelProperty(value = "性别")
    private String sex;
    @ApiModelProperty(value = "民族")
    private String nation;
    @ApiModelProperty(value = "出生日期")
    private String birth;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "身份证号")
    private String id;
    @ApiModelProperty(value = "发证机关")
    private String authority;
    @ApiModelProperty(value = "证件有效期")
    private String validDate;
}
