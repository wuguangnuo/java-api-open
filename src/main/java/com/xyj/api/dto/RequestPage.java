package com.xyj.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 默认分页
 */
public class RequestPage {
    @ApiModelProperty(value = "分页大小")
    private int pageSize;

    @ApiModelProperty(value = "分页页码(第一页填1)")
    private int pageIndex;

    // 默认分页大小
    public int getPageSize() {
        return pageSize <= 0 ? 10 : pageSize;
    }

    // 默认分页第几页
    public int getPageIndex() {
        return pageIndex <= 0 ? 0 : pageIndex;
    }
}
