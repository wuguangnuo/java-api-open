package com.xyj.api.utils;

import com.xyj.api.utils.untilsInterface.CommonEnum;
import org.springframework.stereotype.Component;

@Component
public class EummUtil {
    /**
     * 获取枚举值
     *
     * @param code 枚举名称
     * @param clazz Enum.class
     * @param <T> Enum
     * @return 枚举值
     */
    public static <T extends CommonEnum> T getEnumBycode(Class<T> clazz, int code) {
        for (T e : clazz.getEnumConstants())
            if (code == e.getCode())
                return e;
        return null;
    }

    public static <T extends Enum<?>> T getEnumByName(String name, Class<T> clazz) {
        if (!clazz.isEnum()) {
            return null;
        }
        try {
            T[] enumConstants = clazz.getEnumConstants();
            for (T ec : enumConstants) {
                if (((Enum<?>) ec).name().equals(name)) {
                    return ec;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends Enum<?>, S extends Enum<?>> T convertEnum(S source, Class<T> targetClass) {
        if (source instanceof Enum) {
            String sourceEnum = ((Enum<?>) source).name();
            try {
                return getEnumByName(sourceEnum, targetClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        return null;

    }
}
