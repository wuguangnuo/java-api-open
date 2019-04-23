package com.xyj.api.enums.common;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.xyj.api.utils.untilsInterface.CommonEnum;

/**
 * 证件类型枚举
 */
public enum CertificateEnum implements CommonEnum {
    身份证(1),
    驾驶证(2),
    健康证(3),
    月嫂证(4),
    母婴护理师证(5),
    育婴师证(6),
    护士证(7),
    签证(8),
    英语等级证(9),
    其他证件(99);

    @EnumValue
    private int value;

    private CertificateEnum(int value) {
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
