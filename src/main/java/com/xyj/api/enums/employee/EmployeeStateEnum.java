package com.xyj.api.enums.employee;

public enum EmployeeStateEnum {
    上架(1),
    下架(2),
    离职(3);
    private int value;

    private EmployeeStateEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
