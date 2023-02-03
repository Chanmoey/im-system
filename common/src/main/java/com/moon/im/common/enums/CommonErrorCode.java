package com.moon.im.common.enums;

import com.moon.im.common.exception.ApplicationExceptionEnum;

/**
 * @author Chanmoey
 * @date 2023年02月04日
 */
public enum CommonErrorCode implements ApplicationExceptionEnum {

    SERVER_ERROR(500, "服务器通用异常");

    private final int code;
    private final String error;

    CommonErrorCode(int code, String error) {
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
