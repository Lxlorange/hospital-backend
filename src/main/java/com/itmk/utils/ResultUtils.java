package com.itmk.utils;

public class ResultUtils {
    public static ResultVo succcess() {
        return Vo((String)null, 200, (Object)null);
    }

    public static ResultVo success(String msg) {
        return Vo(msg, 200, (Object)null);
    }

    public static ResultVo success(String msg, Object data) {
        return Vo(msg, 200, data);
    }

    public static ResultVo success(String msg, int code, Object data) {
        return Vo(msg, code, data);
    }

    public static ResultVo Vo(String msg, int code, Object data) {
        return new ResultVo(msg, code, data);
    }

    public static ResultVo error() {
        return Vo((String)null, 500, (Object)null);
    }

    public static ResultVo error(String msg) {
        return Vo(msg, 500, (Object)null);
    }

    public static ResultVo error(String msg, int code, Object data) {
        return Vo(msg, code, data);
    }

    public static ResultVo error(String msg, int code) {
        return Vo(msg, code, (Object)null);
    }

    public static ResultVo error(String msg, Object data) {
        return Vo(msg, 500, data);
    }
}
