package com.moon.im.service.friendship.service;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.friendship.model.req.AddFriendShipGroupMemberReq;
import com.moon.im.service.friendship.model.req.DeleteFriendShipGroupMemberReq;
import com.moon.im.service.friendship.model.resp.AddFriendShipGroupMemberResp;


public interface ImFriendShipGroupMemberService {

    AddFriendShipGroupMemberResp addGroupMember(AddFriendShipGroupMemberReq req);

    void delGroupMember(DeleteFriendShipGroupMemberReq req);

    int doAddGroupMember(Long groupId, String toId);

    int clearGroupMember(Long groupId);
}
