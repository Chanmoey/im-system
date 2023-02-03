package com.moon.im.service.group.service;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.group.dao.ImGroupMemberEntity;
import com.moon.im.service.group.model.req.*;
import com.moon.im.service.group.model.resp.AddMemberResp;
import com.moon.im.service.group.model.resp.GetRoleInGroupResp;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年02月03日
 */
public interface ImGroupMemberService {

    List<AddMemberResp> importGroupMembers(ImportGroupMemberReq req);

    void addGroupMember(String groupId, Integer appId, GroupMemberDto dto);

    GetRoleInGroupResp getRoleInGroupOne(String groupId, String memberId, Integer appId);

    List<GroupMemberDto> getGroupMembers(String groupId, Integer appId);

    List<String> getMemberJoinedGroupIds(GetJoinedGroupReq req);

    void transferGroupMember(String owner, String groupId, Integer appId);

    List<AddMemberResp> addMember(AddGroupMemberReq req);

    void removeMember(RemoveGroupMemberReq req);

    void removeGroupMember(String groupId, Integer appId, String memberId);
}
