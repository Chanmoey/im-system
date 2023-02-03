package com.moon.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moon.im.common.constant.DBColumn;
import com.moon.im.common.enums.FriendShipErrorCode;
import com.moon.im.common.enums.FriendShipStatusEnum;
import com.moon.im.common.exception.ApplicationException;
import com.moon.im.service.friendship.dao.ImFriendShipEntity;
import com.moon.im.service.friendship.dao.ImFriendShipGroupEntity;
import com.moon.im.service.friendship.dao.ImFriendShipGroupMemberEntity;
import com.moon.im.service.friendship.dao.mapper.ImFriendShipGroupMemberMapper;
import com.moon.im.service.friendship.model.req.AddFriendShipGroupMemberReq;
import com.moon.im.service.friendship.model.req.DeleteFriendShipGroupMemberReq;
import com.moon.im.service.friendship.model.resp.AddFriendShipGroupMemberResp;
import com.moon.im.service.friendship.service.ImFriendShipGroupMemberService;
import com.moon.im.service.friendship.service.ImFriendShipGroupService;
import com.moon.im.service.friendship.service.ImFriendShipService;
import com.moon.im.service.user.service.ImUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class ImFriendShipGroupMemberServiceImpl implements ImFriendShipGroupMemberService {

    @Autowired
    private ImFriendShipGroupMemberMapper imFriendShipGroupMemberMapper;

    @Autowired
    private ImFriendShipGroupService imFriendShipGroupService;


    @Autowired
    private ImFriendShipService imFriendShipService;

    @Autowired
    ImFriendShipGroupMemberService thisService;

    @Override
    @Transactional
    public AddFriendShipGroupMemberResp addGroupMember(AddFriendShipGroupMemberReq req) {

        ImFriendShipGroupEntity group = imFriendShipGroupService.getGroup(req.getFromId(), req.getGroupName(), req.getAppId());

        if (group == null) {
            throw new ApplicationException(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_NOT_EXIST);
        }

        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();
        for (String toId : req.getToIds()) {
            try {
                // 只有是好友才能添加
                ImFriendShipEntity relation = imFriendShipService.getRelation(req.getFromId(), toId, req.getAppId());
                if (FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode() != relation.getStatus()) {
                    errorId.add(toId);
                    continue;
                }
                int i = thisService.doAddGroupMember(group.getGroupId(), toId);
                if (i == 1) {
                    successId.add(toId);
                } else {
                    errorId.add(toId);
                }
            } catch (Exception e) {
                errorId.add(toId);
            }
        }

        AddFriendShipGroupMemberResp resp = new AddFriendShipGroupMemberResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);

        return resp;
    }

    @Override
    public void delGroupMember(DeleteFriendShipGroupMemberReq req) {
        ImFriendShipGroupEntity group = imFriendShipGroupService
                .getGroup(req.getFromId(), req.getGroupName(), req.getAppId());
        if (group == null) {
            throw new ApplicationException(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_NOT_EXIST);
        }

        deleteGroupMembers(group.getGroupId(), req.getToIds());
    }

    @Override
    @Transactional
    public int doAddGroupMember(Long groupId, String toId) {
        ImFriendShipGroupMemberEntity imFriendShipGroupMemberEntity = new ImFriendShipGroupMemberEntity();
        imFriendShipGroupMemberEntity.setGroupId(groupId);
        imFriendShipGroupMemberEntity.setToId(toId);
        try {
            return imFriendShipGroupMemberMapper.insert(imFriendShipGroupMemberEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 物理删除
     */
    public int deleteGroupMembers(Long groupId, List<String> toIds) {
        QueryWrapper<ImFriendShipGroupMemberEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DBColumn.GROUP_ID, groupId);
        queryWrapper.in(DBColumn.TO_ID, toIds);

        try {
            return imFriendShipGroupMemberMapper.delete(queryWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int clearGroupMember(Long groupId) {
        QueryWrapper<ImFriendShipGroupMemberEntity> query = new QueryWrapper<>();
        query.eq(DBColumn.GROUP_ID, groupId);
        return imFriendShipGroupMemberMapper.delete(query);
    }
}
