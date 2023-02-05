package com.moon.im.common.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
@Data
public class RequestBase {

    @NotNull(message = "appId不能为空")
    private Integer appId;

    private String operater;

    private Integer clientType;

    private String imei;
}
