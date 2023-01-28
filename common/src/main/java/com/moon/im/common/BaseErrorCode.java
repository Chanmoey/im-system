package com.moon.im.common;

import com.moon.im.common.exception.ApplicationExceptionEnum;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
public enum BaseErrorCode implements ApplicationExceptionEnum {

    SUCCESS(200,"success"),
    SYSTEM_ERROR(90000,"服务器内部错误,请联系管理员"),
    PARAMETER_ERROR(90001,"参数校验错误");

    private final int code;
    private final String error;

    BaseErrorCode(int code, String error){
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
