package com.moon.im.service.friendship.service;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.friendship.dao.ImFriendShipGroupEntity;
import com.moon.im.service.friendship.model.req.AddFriendShipGroupMemberReq;
import com.moon.im.service.friendship.model.req.DeleteFriendShipGroupMemberReq;


public interface ImFriendShipGroupMemberService {

    ResponseVO addGroupMember(AddFriendShipGroupMemberReq req);

    ResponseVO delGroupMember(DeleteFriendShipGroupMemberReq req);

    int doAddGroupMember(Long groupId, String toId);

    int clearGroupMember(Long groupId);
}
