package com.xyj.api.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 城市DTO
 */
@Data
public class CityDto {
    @ApiModelProperty(value = "城市ID")
    private int id;
    @ApiModelProperty(value = "城市名称")
    private String name;
    @ApiModelProperty(value = "地区")
    private List<AreaDto> areas;
}
