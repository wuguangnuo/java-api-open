package com.xyj.api.enums.common;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 通用字典枚举
 */
public enum DictionaryEnum {
    支付方式(1),
    消费方式(2),
    银行(3),
    发票类型(4),
    发票快递(5),
    发票项目(6),
    车辆类型(7),
    车辆大小(8),
    发票税点(9),
    快递公司(10),
    企业助手信息类型(11),
    知识类型(12),
    保险供应商(13),
    保姆能力(20),
    保姆工作模式(21),
    保姆照顾老人(22),
    保姆照顾小孩(23),
    保姆做饭技能(24),
    保姆其它技能(25),
    投诉类型(111);

    @EnumValue
    private int status;

    private DictionaryEnum(int value) {
        this.status = value;
    }

    public int getValue() {
        return status;
    }

    public void setValue(int value) {
        this.status = value;
    }
}