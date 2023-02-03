package com.moon.im.service.friendship.service;

import com.moon.im.service.friendship.dao.ImFriendShipGroupEntity;
import com.moon.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.moon.im.service.friendship.model.req.DeleteFriendShipGroupReq;

public interface ImFriendShipGroupService {

    void addGroup(AddFriendShipGroupReq req);

    void deleteGroup(DeleteFriendShipGroupReq req);

    ImFriendShipGroupEntity getGroup(String fromId, String groupName);

}
