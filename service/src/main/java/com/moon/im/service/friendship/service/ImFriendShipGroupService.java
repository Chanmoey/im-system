package com.moon.im.service.friendship.service;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.friendship.dao.ImFriendShipGroupEntity;
import com.moon.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.moon.im.service.friendship.model.req.DeleteFriendShipGroupReq;

public interface ImFriendShipGroupService {

    ResponseVO<Object> addGroup(AddFriendShipGroupReq req);

    ResponseVO<Object> deleteGroup(DeleteFriendShipGroupReq req);

    ResponseVO<ImFriendShipGroupEntity> getGroup(String fromId, String groupName, Integer appId);

}
