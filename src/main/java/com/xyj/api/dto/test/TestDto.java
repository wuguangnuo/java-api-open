package com.xyj.api.dto.test;

import com.xyj.api.dto.RequestPage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TestDto extends RequestPage {
    @ApiModelProperty("common")
    private String test;
}
