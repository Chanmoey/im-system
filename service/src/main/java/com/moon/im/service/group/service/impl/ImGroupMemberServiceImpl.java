package com.moon.im.service.group.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.moon.im.common.constant.DBColumn;
import com.moon.im.common.enums.GroupErrorCode;
import com.moon.im.common.enums.GroupMemberRoleEnum;
import com.moon.im.common.enums.GroupTypeEnum;
import com.moon.im.common.exception.ApplicationException;
import com.moon.im.service.group.dao.ImGroupEntity;
import com.moon.im.service.group.dao.ImGroupMemberEntity;
import com.moon.im.service.group.dao.mapper.ImGroupMemberMapper;
import com.moon.im.service.group.model.req.*;
import com.moon.im.service.group.model.resp.AddMemberResp;
import com.moon.im.service.group.model.resp.GetRoleInGroupResp;
import com.moon.im.service.group.service.ImGroupMemberService;
import com.moon.im.service.group.service.ImGroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年02月03日
 */
@Service
public class ImGroupMemberServiceImpl implements ImGroupMemberService {

    @Autowired
    private ImGroupService imGroupService;

    @Autowired
    private ImGroupMemberMapper imGroupMemberMapper;

    @Autowired
    private ImGroupMemberService thisService;

    @Autowired
    ImGroupMemberService groupMemberService;

    public List<AddMemberResp> importGroupMembers(ImportGroupMemberReq req) {
        List<AddMemberResp> resp = new ArrayList<>();
        imGroupService.getGroup(req.getGroupId(), req.getAppId());

        for (GroupMemberDto dto : req.getMembers()) {
            AddMemberResp addMemberResp = new AddMemberResp();
            addMemberResp.setMemberId(dto.getMemberId());
            try {
                thisService.addGroupMember(req.getGroupId(), req.getAppId(), dto);
                addMemberResp.setResult(0);
            } catch (ApplicationException e) {
                if (GroupErrorCode.USER_IS_JOINED_GROUP.getCode() == e.getCode()) {
                    addMemberResp.setResult(2);
                    addMemberResp.setResultMessage(e.getMessage());
                } else {
                    addMemberResp.setResult(1);
                    addMemberResp.setResultMessage(e.getMessage());
                }
            }
            resp.add(addMemberResp);
        }

        return resp;
    }

    @Override
    public void addGroupMember(String groupId,
                               Integer appId, GroupMemberDto dto) {
        // 保证群主唯一
        if (dto.getRole() != null && GroupMemberRoleEnum.OWNER.getCode() == dto.getRole()) {
            QueryWrapper<ImGroupMemberEntity> queryOwner = new QueryWrapper<>();
            queryOwner.eq(DBColumn.GROUP_ID, groupId);
            queryOwner.eq(DBColumn.APP_ID, appId);
            queryOwner.eq(DBColumn.ROLE, GroupMemberRoleEnum.OWNER.getCode());
            Integer ownerNum = imGroupMemberMapper.selectCount(queryOwner);
            if (ownerNum > 0) {
                throw new ApplicationException(GroupErrorCode.GROUP_IS_HAVE_OWNER);
            }
        }

        // 是否在群内
        QueryWrapper<ImGroupMemberEntity> query = new QueryWrapper<>();
        query.eq(DBColumn.GROUP_ID, groupId);
        query.eq(DBColumn.APP_ID, appId);
        query.eq(DBColumn.MEMBER_ID, dto.getMemberId());
        ImGroupMemberEntity member = imGroupMemberMapper.selectOne(query);

        long now = System.currentTimeMillis();
        if (member == null) {
            // 初次加群
            member = new ImGroupMemberEntity();
            BeanUtils.copyProperties(dto, member);
            member.setGroupId(groupId);
            member.setAppId(appId);
            member.setJoinTime(now);
            try {
                int inset = imGroupMemberMapper.insert(member);
                if (inset != 1) {
                    throw new ApplicationException(GroupErrorCode.USER_JOIN_GROUP_ERROR);
                }
            } catch (Exception e) {
                throw new ApplicationException(GroupErrorCode.USER_JOIN_GROUP_ERROR);
            }
        } else if (GroupMemberRoleEnum.LEAVE.getCode() == member.getRole()) {
            // 重新进群
            member = new ImGroupMemberEntity();
            BeanUtils.copyProperties(dto, member);
            member.setJoinTime(now);
            try {
                int update = imGroupMemberMapper.update(member, query);
                if (update != 1) {
                    throw new ApplicationException(GroupErrorCode.USER_JOIN_GROUP_ERROR);
                }
            } catch (Exception e) {
                throw new ApplicationException(GroupErrorCode.USER_JOIN_GROUP_ERROR);
            }
        } else {
            // 早已入群
            throw new ApplicationException(GroupErrorCode.USER_IS_JOINED_GROUP);
        }
    }

    @Override
    public GetRoleInGroupResp getRoleInGroupOne(String groupId, String memberId, Integer appId) {
        QueryWrapper<ImGroupMemberEntity> query = new QueryWrapper<>();
        query.eq(DBColumn.GROUP_ID, groupId);
        query.eq(DBColumn.APP_ID, appId);
        query.eq(DBColumn.MEMBER_ID, memberId);
        ImGroupMemberEntity member = imGroupMemberMapper.selectOne(query);

        if (member == null || member.getRole() == GroupMemberRoleEnum.LEAVE.getCode()) {
            throw new ApplicationException(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }

        GetRoleInGroupResp resp = new GetRoleInGroupResp();
        resp.setGroupMemberId(member.getGroupMemberId());
        resp.setMemberId(member.getMemberId());
        resp.setRole(member.getRole());
        resp.setSpeakDate(member.getSpeakDate());
        return resp;
    }

    @Override
    public List<GroupMemberDto> getGroupMembers(String groupId, Integer appId) {
        return this.imGroupMemberMapper.getGroupMember(appId, groupId);
    }

    @Override
    public List<String> getMemberJoinedGroupIds(GetJoinedGroupReq req) {
        return imGroupMemberMapper.getJoinedGroupIds(req.getAppId(), req.getMemberId());
    }

    @Override
    public void transferGroupMember(String owner, String groupId, Integer appId) {
        //更新旧群主
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        imGroupMemberEntity.setRole(GroupMemberRoleEnum.ORDINARY.getCode());
        UpdateWrapper<ImGroupMemberEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(DBColumn.APP_ID, appId);
        updateWrapper.eq(DBColumn.GROUP_ID, groupId);
        updateWrapper.eq(DBColumn.ROLE, GroupMemberRoleEnum.OWNER.getCode());
        imGroupMemberMapper.update(imGroupMemberEntity, updateWrapper);

        //更新新群主
        ImGroupMemberEntity newOwner = new ImGroupMemberEntity();
        newOwner.setRole(GroupMemberRoleEnum.OWNER.getCode());
        UpdateWrapper<ImGroupMemberEntity> ownerWrapper = new UpdateWrapper<>();
        ownerWrapper.eq(DBColumn.APP_ID, appId);
        ownerWrapper.eq(DBColumn.GROUP_ID, groupId);
        ownerWrapper.eq(DBColumn.MEMBER_ID, owner);
        imGroupMemberMapper.update(newOwner, ownerWrapper);
    }

    /**
     * 拉人入群，私人群（微信群）使用
     */
    @Override
    public List<AddMemberResp> addMember(AddGroupMemberReq req) {
        List<AddMemberResp> resp = new ArrayList<>();

        boolean isAdmin = false;

        ImGroupEntity group = imGroupService.getGroup(req.getGroupId(), req.getAppId());

        if (group == null) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        if (!isAdmin && GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {
            throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_APP_MANAGER_ROLE);
        }

        for (GroupMemberDto dto : req.getMembers()) {
            AddMemberResp addMemberResp = new AddMemberResp();
            addMemberResp.setMemberId(dto.getMemberId());
            try {
                thisService.addGroupMember(req.getGroupId(), req.getAppId(), dto);
                addMemberResp.setResult(0);
            } catch (ApplicationException e) {
                if (GroupErrorCode.USER_IS_JOINED_GROUP.getCode() == e.getCode()) {
                    addMemberResp.setResult(2);
                    addMemberResp.setResultMessage(e.getMessage());
                } else {
                    addMemberResp.setResult(1);
                    addMemberResp.setResultMessage(e.getMessage());
                }
            }
            resp.add(addMemberResp);
        }

        return resp;
    }

    /**
     * 踢人出群
     */
    @Override
    public void removeMember(RemoveGroupMemberReq req) {
        boolean isAdmin = false;
        ImGroupEntity group = imGroupService.getGroup(req.getGroupId(), req.getAppId());
        if (group == null) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        if (!isAdmin) {
            //获取操作人的权限 是管理员or群主or群成员
            GetRoleInGroupResp role = getRoleInGroupOne(req.getGroupId(), req.getOperater(), req.getAppId());
            if (role == null) {
                throw new ApplicationException(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
            }
            Integer roleInfo = role.getRole();

            boolean isOwner = roleInfo == GroupMemberRoleEnum.OWNER.getCode();
            boolean isManager = roleInfo == GroupMemberRoleEnum.MANAGER.getCode();

            if (!isOwner && !isManager) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }

            //私有群必须是群主才能踢人
            if (!isOwner && GroupTypeEnum.PRIVATE.getCode() == group.getGroupType()) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }

            //公开群管理员和群主可踢人，但管理员只能踢普通群成员
            if (GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {
                //获取被踢人的权限
                GetRoleInGroupResp memberRole = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
                if (memberRole == null) {
                    throw new ApplicationException(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
                }
                if (memberRole.getRole() == GroupMemberRoleEnum.OWNER.getCode()) {
                    throw new ApplicationException(GroupErrorCode.GROUP_OWNER_IS_NOT_REMOVE);
                }
                //是管理员并且被踢人不是群成员，无法操作
                if (isManager && memberRole.getRole() != GroupMemberRoleEnum.ORDINARY.getCode()) {
                    throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                }
            }

        }
        groupMemberService.removeGroupMember(req.getGroupId(), req.getAppId(), req.getMemberId());

    }

    @Override
    public void removeGroupMember(String groupId, Integer appId, String memberId) {
        GetRoleInGroupResp roleInGroupOne = getRoleInGroupOne(groupId, memberId, appId);
        if (roleInGroupOne == null) {
            throw new ApplicationException(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        imGroupMemberEntity.setRole(GroupMemberRoleEnum.LEAVE.getCode());
        imGroupMemberEntity.setGroupMemberId(roleInGroupOne.getGroupMemberId());
        try {
            int update = imGroupMemberMapper.updateById(imGroupMemberEntity);
            if (update != 1) {
                throw new ApplicationException(GroupErrorCode.REMOVE_GROUP_MEMBER_ERROR);
            }
        } catch (Exception e) {
            throw new ApplicationException(GroupErrorCode.REMOVE_GROUP_MEMBER_ERROR);
        }
    }

    @Override
    public void updateGroupMember(UpdateGroupMemberReq req) {
        boolean isAdmin = false;
        ImGroupEntity group = imGroupService.getGroup(req.getGroupId(), req.getAppId());
        if (group == null) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        //是否是自己修改自己的资料
        boolean isMeOperate = req.getOperater().equals(req.getMemberId());

        if (!isAdmin) {
            //昵称只能自己修改 权限只能群主或管理员修改
            if (StringUtils.isBlank(req.getAlias()) && !isMeOperate) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_ONESELF);
            }
            //私有群不能设置管理员
            if (group.getGroupType() == GroupTypeEnum.PRIVATE.getCode() &&
                    req.getRole() != null && (req.getRole() == GroupMemberRoleEnum.MANAGER.getCode() ||
                    req.getRole() == GroupMemberRoleEnum.OWNER.getCode())) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }

            GetRoleInGroupResp roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getOperater(), req.getAppId());
            if (roleInGroupOne == null) {
                throw new ApplicationException(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
            }
            Integer roleInfo = roleInGroupOne.getRole();
            boolean isOwner = roleInfo == GroupMemberRoleEnum.OWNER.getCode();
            boolean isManager = roleInfo == GroupMemberRoleEnum.MANAGER.getCode();

            //不是管理员不能修改权限
            if (req.getRole() != null && !isOwner && !isManager) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }
            //管理员只有群主能够设置
            if (req.getRole() != null && req.getRole() == GroupMemberRoleEnum.MANAGER.getCode() && !isOwner) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }

        ImGroupMemberEntity update = new ImGroupMemberEntity();
        if (StringUtils.isNotBlank(req.getAlias())) {
            update.setAlias(req.getAlias());
        }

        if (req.getRole() != null) {
            update.setRole(req.getRole());
        }

        UpdateWrapper<ImGroupMemberEntity> objectUpdateWrapper = new UpdateWrapper<>();
        objectUpdateWrapper.eq(DBColumn.APP_ID, req.getAppId());
        objectUpdateWrapper.eq(DBColumn.MEMBER_ID, req.getMemberId());
        objectUpdateWrapper.eq(DBColumn.GROUP_ID, req.getGroupId());
        imGroupMemberMapper.update(update, objectUpdateWrapper);
    }

    @Override
    public void speak(SpeaMemberReq req) {
        ImGroupEntity group = imGroupService.getGroup(req.getGroupId(), req.getAppId());
        if (group == null) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        boolean isAdmin = false;
        boolean isOwner;
        boolean isManager;
        GetRoleInGroupResp memberRole = null;

        if (!isAdmin) {

            //获取操作人的权限 是管理员or群主or群成员
            GetRoleInGroupResp role = getRoleInGroupOne(req.getGroupId(), req.getOperater(), req.getAppId());
            if (role == null) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }

            Integer roleInfo = role.getRole();

            isOwner = roleInfo == GroupMemberRoleEnum.OWNER.getCode();
            isManager = roleInfo == GroupMemberRoleEnum.MANAGER.getCode();

            if (!isOwner && !isManager) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }

            //获取被操作的权限
            memberRole = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
            if (memberRole == null) {
                throw new ApplicationException(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
            }
            //被操作人是群主只能app管理员操作
            if (memberRole.getRole() == GroupMemberRoleEnum.OWNER.getCode()) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_APP_MANAGER_ROLE);
            }

            // 操作人是管理员并且被操作人不是群成员，无法操作
            if (isManager && memberRole.getRole() != GroupMemberRoleEnum.ORDINARY.getCode()) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }

        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        imGroupMemberEntity.setGroupMemberId(memberRole.getGroupMemberId());
        if (req.getSpeakDate() > 0) {
            imGroupMemberEntity.setSpeakDate(System.currentTimeMillis() + req.getSpeakDate());
        } else {
            imGroupMemberEntity.setSpeakDate(req.getSpeakDate());
        }

        try {
            int update = imGroupMemberMapper.updateById(imGroupMemberEntity);
            if (update != 1) {
                throw new ApplicationException(GroupErrorCode.MUTE_MEMBER_ERROR);
            }
        } catch (Exception e) {
            throw new ApplicationException(GroupErrorCode.MUTE_MEMBER_ERROR);
        }
    }
}
