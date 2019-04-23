package com.xyj.api.enums.employee;

public enum EmployeeLevelEnum {
    一星(1),
    二星(2),
    三星(3),
    四星(4),
    五星(5);
    private int value;

    private EmployeeLevelEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
