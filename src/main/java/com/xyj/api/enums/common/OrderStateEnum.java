package com.xyj.api.enums.common;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.xyj.api.utils.untilsInterface.CommonEnum;

/**
 * 订单状态枚举 OrderStateEnum
 */
public enum OrderStateEnum implements CommonEnum {
    已接单(10),
    派单待确认(20),
    拒绝接单(30),
    已派单(40),
    执行中(50),
    开始服务(60),
    服务结束(70),
    已完成(80),
    已评价(90),
    已取消(99);

    @EnumValue
    private int value;

    private OrderStateEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public int getCode() {
        return value;
    }
}

