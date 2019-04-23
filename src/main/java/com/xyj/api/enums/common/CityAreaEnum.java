package com.xyj.api.enums.common;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 城市地区状态枚举(禁用/正常)
 */
public enum CityAreaEnum {
    正常(1),
    禁用(2);

    @EnumValue
    private int status;

    private CityAreaEnum(int value) {
        this.status = value;
    }

    public int getValue() {
        return status;
    }

    public void setValue(int value) {
        this.status = value;
    }
}