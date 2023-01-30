package com.moon.im.common.enums;

public enum ApproverFriendRequestStatusEnum {

    UNAUDITED(0),
    /**
     * 1 同意；2 拒绝。
     */
    AGREE(1),

    REJECT(2),
    ;

    private final int code;

    ApproverFriendRequestStatusEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
