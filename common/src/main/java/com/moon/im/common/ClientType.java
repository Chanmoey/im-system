package com.moon.im.common;

/**
 * 登录的设备枚举
 *
 * @author Chanmoey
 **/
public enum ClientType {

    WEBAPI(0, "webApi"),
    WEB(1, "web"),
    IOS(2, "ios"),
    ANDROID(3, "android"),
    WINDOWS(4, "windows"),
    MAC(5, "mac"),
    ;

    private final int code;
    private final String error;

    ClientType(int code, String error) {
        this.code = code;
        this.error = error;
    }

    public int getCode() {
        return this.code;
    }

    public String getError() {
        return this.error;
    }


}
