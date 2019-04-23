package com.xyj.api.enums.employee;

public enum WorkingPropertysEnum {
    全职(1),
    兼职(2);
    private int value;

    private WorkingPropertysEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
