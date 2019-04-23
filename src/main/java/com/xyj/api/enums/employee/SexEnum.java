package com.xyj.api.enums.employee;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum SexEnum {
    请选择(1),
    男(2),
    女(3),
    未知(4);
    @EnumValue
    private int status;

    private SexEnum(int value) {
        this.status = value;
    }

    public int getValue() {
        return status;
    }

    public void setValue(int value) {
        this.status = value;
    }
}
