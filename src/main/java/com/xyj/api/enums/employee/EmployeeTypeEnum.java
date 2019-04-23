package com.xyj.api.enums.employee;

public enum EmployeeTypeEnum {
    服务员工(10),
    支持员工(20);
    private int value;

    private EmployeeTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
