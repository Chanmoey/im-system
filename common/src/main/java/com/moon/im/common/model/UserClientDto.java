package com.moon.im.common.model;

import lombok.Data;

/**
 * 用户登录的标识，key
 *
 * @author Chanmoey
 * @date 2023年02月02日
 */
@Data
public class UserClientDto {

    private Integer appId;

    private Integer clientType;

    private String userId;

    private String imei;
}
