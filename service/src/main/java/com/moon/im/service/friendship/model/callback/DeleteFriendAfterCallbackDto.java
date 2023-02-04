package com.moon.im.service.friendship.model.callback;

import lombok.Data;


@Data
public class DeleteFriendAfterCallbackDto {

    private String fromId;

    private String toId;
}
