package com.moon.im.service.friendship.service;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.friendship.model.req.ApproveFriendRequestReq;
import com.moon.im.service.friendship.model.req.FriendDto;
import com.moon.im.service.friendship.model.req.ReadFriendShipRequestReq;

/**
 * @author Chanmoey
 * @date 2023年01月31日
 */
public interface ImFriendShipRequestService {

    ResponseVO<Object> addFriendshipRequest(String fromId, FriendDto dto, Integer appId);

    ResponseVO<Object> approveFriendshipRequest(ApproveFriendRequestReq req);

    ResponseVO<Object> readFriendShipRequest(ReadFriendShipRequestReq req);

    ResponseVO<Object> getFriendShipRequest(ReadFriendShipRequestReq req);
}
