package com.moon.im.service.friendship.service;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.friendship.model.req.FriendDto;

/**
 * @author Chanmoey
 * @date 2023年01月31日
 */
public interface ImFriendShipRequestService {

    ResponseVO<Object> addFriendshipRequest(String fromId, FriendDto dto, Integer appId);
}
