package com.moon.im.common.enums;

public enum RequestFriendReadStatusEnum {

    /**
     * 验证
     */
    READ(1),

    /**
     * 不需要验证
     */
    UNREAD(0),

    ;


    private final int code;

    RequestFriendReadStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
