package com.moon.im.service.friendship.service;

import com.moon.im.service.friendship.dao.ImFriendShipRequestEntity;
import com.moon.im.service.friendship.model.req.ApproveFriendRequestReq;
import com.moon.im.service.friendship.model.req.FriendDto;
import com.moon.im.service.friendship.model.req.ReadFriendShipRequestReq;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年01月31日
 */
public interface ImFriendShipRequestService {

    void addFriendshipRequest(String fromId, FriendDto dto, Integer appId);

    void approveFriendshipRequest(ApproveFriendRequestReq req);

    void readFriendShipRequest(ReadFriendShipRequestReq req);

    List<ImFriendShipRequestEntity> getFriendShipRequest(ReadFriendShipRequestReq req);
}
