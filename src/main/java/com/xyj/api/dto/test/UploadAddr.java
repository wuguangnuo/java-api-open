package com.xyj.api.dto.test;

import com.xyj.api.dto.LocData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 员工上传地理位置到MongoDB
 */
@Data
@ApiModel(value = "上传地理位置")
public class UploadAddr extends LocData {
    @NotEmpty(message = "不能为空")
    @ApiModelProperty(value = "具体位置")
    private String addr;
}
