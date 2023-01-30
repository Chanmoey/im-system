package com.moon.im.service.friendship.model.resp;

import lombok.Data;

/**
 * @author Chanmoey
 * @date 2023年01月30日
 */
@Data
public class CheckFriendShipResp {

    private String fromId;

    private String toId;

    private Integer status;
}
