package com.xyj.api.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static java.net.HttpURLConnection.*;

@Data
@Slf4j
public class ApiRes<T> {
    private Meta Meta;
    private T Data;

    public static <T> ApiRes<T> suc(String msg) {
        return suc(msg, null);
    }

    public static <T> ApiRes<T> suc(T obj) {
        return suc("Success", obj);
    }

    public static <T> ApiRes<T> suc(String msg, T obj) {
        return res(HTTP_OK, msg, obj);
    }

    public static <T> ApiRes<T> fail(String msg) {
        log.warn("ApiRes Fail: " + msg);
        return res(HTTP_NOT_IMPLEMENTED, msg, null);
    }

    public static <T> ApiRes<T> err(String msg) {
        log.error("ApiRes Error: " + msg);
        return res(HTTP_INTERNAL_ERROR, msg, null);
    }

    public static <T> ApiRes<T> res(int state, String msg, T data) {
        Meta m = new Meta();
        m.setState(state);
        m.setMsg(msg);
        return res(m, data);
    }

    public static <T> ApiRes<T> res(Meta meta, T data) {
        ApiRes<T> result = new ApiRes<>();
        result.Meta = meta;
        result.Data = data;
        return result;
    }
}
