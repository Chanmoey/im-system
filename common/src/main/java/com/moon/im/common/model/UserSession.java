package com.moon.im.common.model;

import lombok.Data;

/**
 * @author Chanmoey
 * @date 2023年02月01日
 */
@Data
public class UserSession {

    private String userId;

    private Integer appId;

    /**
     * 端标志
     */
    private Integer clientType;

    /**
     * sdk版本号
     */
    private Integer version;

    /**
     * 连接状态 1在线、2离线
     */
    private Integer connectStatus;
}
