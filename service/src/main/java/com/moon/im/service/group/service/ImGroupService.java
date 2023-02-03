package com.moon.im.service.group.service;

import com.moon.im.service.group.dao.ImGroupEntity;
import com.moon.im.service.group.model.req.*;
import com.moon.im.service.group.model.resp.GetGroupResp;
import com.moon.im.service.group.model.resp.GetJoinedGroupResp;

/**
 * @author Chanmoey
 * @date 2023年02月03日
 */
public interface ImGroupService {

    void importGroup(ImportGroupReq req);

    ImGroupEntity getGroup(String groupId, Integer appId);

    void updateGroupInfo(UpdateGroupReq req);

    void createGroup(CreateGroupReq req);

    GetGroupResp getGroupInfo(GetGroupReq req);

    GetJoinedGroupResp getJoinedGroup(GetJoinedGroupReq req);

    void destroyGroup(DestroyGroupReq req);

    void transferGroup(TransferGroupReq req);
}
