package com.moon.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moon.im.common.ResponseVO;
import com.moon.im.common.constant.DBColumn;
import com.moon.im.service.friendship.dao.ImFriendShipGroupEntity;
import com.moon.im.service.friendship.dao.ImFriendShipGroupMemberEntity;
import com.moon.im.service.friendship.dao.mapper.ImFriendShipGroupMemberMapper;
import com.moon.im.service.friendship.model.req.AddFriendShipGroupMemberReq;
import com.moon.im.service.friendship.model.req.DeleteFriendShipGroupMemberReq;
import com.moon.im.service.friendship.service.ImFriendShipGroupMemberService;
import com.moon.im.service.friendship.service.ImFriendShipGroupService;
import com.moon.im.service.user.dao.ImUserDataEntity;
import com.moon.im.service.user.service.ImUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class ImFriendShipGroupMemberServiceImpl
        implements ImFriendShipGroupMemberService {

    @Autowired
    private ImFriendShipGroupMemberMapper imFriendShipGroupMemberMapper;

    @Autowired
    private ImFriendShipGroupService imFriendShipGroupService;

    @Autowired
    ImUserService imUserService;

    @Autowired
    ImFriendShipGroupMemberService thisService;

    @Override
    @Transactional
    public ResponseVO addGroupMember(AddFriendShipGroupMemberReq req) {

        ResponseVO<ImFriendShipGroupEntity> group = imFriendShipGroupService
                .getGroup(req.getFromId(),req.getGroupName(),req.getAppId());
        if(!group.isOk()){
            return group;
        }

        List<String> successId = new ArrayList<>();
        for (String toId : req.getToIds()) {
            ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(toId, req.getAppId());
            if(singleUserInfo.isOk()){
                int i = thisService.doAddGroupMember(group.getData().getGroupId(), toId);
                if(i == 1){
                    successId.add(toId);
                }
            }
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO delGroupMember(DeleteFriendShipGroupMemberReq req) {
        ResponseVO<ImFriendShipGroupEntity> group = imFriendShipGroupService
                .getGroup(req.getFromId(),req.getGroupName(),req.getAppId());
        if(!group.isOk()){
            return group;
        }

        for (String toId : req.getToIds()) {
            ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(toId, req.getAppId());
            if(singleUserInfo.isOk()){
                int i = deleteGroupMember(group.getData().getGroupId(), req.getToIds());
            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public int doAddGroupMember(Long groupId, String toId) {
        ImFriendShipGroupMemberEntity imFriendShipGroupMemberEntity = new ImFriendShipGroupMemberEntity();
        imFriendShipGroupMemberEntity.setGroupId(groupId);
        imFriendShipGroupMemberEntity.setToId(toId);
        try {
            return imFriendShipGroupMemberMapper.insert(imFriendShipGroupMemberEntity);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 物理删除
     */
    public int deleteGroupMember(Long groupId, List<String> toIds) {
        QueryWrapper<ImFriendShipGroupMemberEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DBColumn.GROUP_ID,groupId);
        queryWrapper.in(DBColumn.TO_ID,toIds);

        try {
            return imFriendShipGroupMemberMapper.delete(queryWrapper);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int clearGroupMember(Long groupId) {
        QueryWrapper<ImFriendShipGroupMemberEntity> query = new QueryWrapper<>();
        query.eq("group_id",groupId);
        return imFriendShipGroupMemberMapper.delete(query);
    }
}
