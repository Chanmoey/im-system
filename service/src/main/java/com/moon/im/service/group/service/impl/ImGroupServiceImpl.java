package com.moon.im.service.group.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.moon.im.common.config.AppConfig;
import com.moon.im.common.constant.CallbackCommand;
import com.moon.im.common.constant.DBColumn;
import com.moon.im.common.enums.*;
import com.moon.im.common.exception.ApplicationException;
import com.moon.im.service.group.dao.ImGroupEntity;
import com.moon.im.service.group.dao.mapper.ImGroupMapper;
import com.moon.im.service.group.model.callback.DestroyGroupCallbackDto;
import com.moon.im.service.group.model.req.*;
import com.moon.im.service.group.model.resp.GetGroupResp;
import com.moon.im.service.group.model.resp.GetJoinedGroupResp;
import com.moon.im.service.group.model.resp.GetRoleInGroupResp;
import com.moon.im.service.group.service.ImGroupMemberService;
import com.moon.im.service.group.service.ImGroupService;
import com.moon.im.service.util.CallbackService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author Chanmoey
 * @date 2023年02月03日
 */
@Service
public class ImGroupServiceImpl implements ImGroupService {

    @Autowired
    private ImGroupMapper imGroupMapper;

    @Autowired
    private ImGroupMemberService imGroupMemberService;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private CallbackService callbackService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importGroup(ImportGroupReq req) {

        if (StringUtils.isNotBlank(req.getGroupId())) {
            QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
            query.eq(DBColumn.APP_ID, req.getAppId());
            query.eq(DBColumn.GROUP_ID, req.getGroupId());
            if (imGroupMapper.selectCount(query) > 0) {
                throw new ApplicationException(GroupErrorCode.GROUP_IS_EXIST);
            }
        } else {
            req.setGroupId(UUID.randomUUID()
                    .toString().replace("-", ""));
        }

        ImGroupEntity imGroupEntity = new ImGroupEntity();
        BeanUtils.copyProperties(req, imGroupEntity);

        if (req.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && StringUtils.isBlank(req.getOwnerId())) {
            throw new ApplicationException(GroupErrorCode.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }

        if (req.getCreateTime() == null) {
            imGroupEntity.setCreateTime(System.currentTimeMillis());
        }
        imGroupEntity.setStatus(GroupStatusEnum.NORMAL.getCode());

        try {
            int insert = imGroupMapper.insert(imGroupEntity);
            if (insert != 1) {
                throw new ApplicationException(GroupErrorCode.IMPORT_GROUP_ERROR);
            }
        } catch (Exception e) {
            throw new ApplicationException(GroupErrorCode.IMPORT_GROUP_ERROR);
        }
    }

    @Override
    public ImGroupEntity getGroup(String groupId, Integer appId) {

        QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
        query.eq(DBColumn.APP_ID, appId);
        query.eq(DBColumn.GROUP_ID, groupId);
        ImGroupEntity imGroupEntity = imGroupMapper.selectOne(query);

        if (imGroupEntity == null) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        return imGroupEntity;
    }

    /**
     * 修改群信息
     */
    @Override
    public void updateGroupInfo(UpdateGroupReq req) {
        ImGroupEntity group = this.getGroup(req.getGroupId(), req.getAppId());
        if (group == null) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        boolean isAdmin = false;

        if (!isAdmin) {
            // 非管理员，需要校验权限
            GetRoleInGroupResp roleInGroupOne = imGroupMemberService
                    .getRoleInGroupOne(req.getGroupId(), req.getOperater(), req.getAppId());

            int role = roleInGroupOne.getRole();

            boolean isManage = role == GroupMemberRoleEnum.MANAGER.getCode();
            boolean isOwner = role == GroupMemberRoleEnum.OWNER.getCode();

            // 公开群(QQ群)只有管理和群主能修改
            if (GroupTypeEnum.PUBLIC.getCode() == group.getGroupType() && !(isManage || isOwner)) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }
        }

        ImGroupEntity imGroupEntity = new ImGroupEntity();
        BeanUtils.copyProperties(req, imGroupEntity);
        QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
        query.eq(DBColumn.APP_ID, req.getAppId());
        query.eq(DBColumn.GROUP_ID, req.getGroupId());

        try {
            int update = imGroupMapper.update(imGroupEntity, query);
            if (update != 1) {
                throw new ApplicationException(GroupErrorCode.UPDATE_GROUP_BASE_INFO_ERROR);
            }
        } catch (Exception e) {
            throw new ApplicationException(GroupErrorCode.UPDATE_GROUP_BASE_INFO_ERROR);
        }

        // 之后回调
        if (appConfig.isModifyGroupAfterCallback()) {
            callbackService.callback(req.getAppId(), CallbackCommand.UPDATE_GROUP_AFTER,
                    JSON.toJSONString(imGroupMapper.selectOne(query)));
        }
    }

    /**
     * 创建群
     */
    @Override
    @Transactional
    public void createGroup(CreateGroupReq req) {

        boolean isAdmin = false;
        if (!isAdmin) {
            req.setOwnerId(req.getOperater());
        }

        //1.判断群id是否存在
        QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();

        if (StringUtils.isEmpty(req.getGroupId())) {
            req.setGroupId(UUID.randomUUID()
                    .toString().replace("-", ""));
        } else {
            query.eq(DBColumn.GROUP_ID, req.getGroupId());
            query.eq(DBColumn.APP_ID, req.getAppId());
            Integer integer = imGroupMapper.selectCount(query);
            if (integer > 0) {
                throw new ApplicationException(GroupErrorCode.GROUP_IS_EXIST);
            }
        }

        if (req.getGroupType() == GroupTypeEnum.PUBLIC.getCode()
                && StringUtils.isBlank(req.getOwnerId())) {
            throw new ApplicationException(GroupErrorCode.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }

        ImGroupEntity imGroupEntity = new ImGroupEntity();
        BeanUtils.copyProperties(req, imGroupEntity);
        imGroupEntity.setCreateTime(System.currentTimeMillis());
        imGroupEntity.setStatus(GroupStatusEnum.NORMAL.getCode());

        try {
            int insert = imGroupMapper.insert(imGroupEntity);
            if (insert != 1) {
                throw new ApplicationException(GroupErrorCode.CREATE_GROUP_ERROR);
            }
        } catch (Exception e) {
            throw new ApplicationException(GroupErrorCode.CREATE_GROUP_ERROR);
        }

        // 插入群主
        GroupMemberDto groupMemberDto = new GroupMemberDto();
        groupMemberDto.setMemberId(req.getOwnerId());
        groupMemberDto.setRole(GroupMemberRoleEnum.OWNER.getCode());
        groupMemberDto.setJoinTime(System.currentTimeMillis());
        imGroupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), groupMemberDto);

        //插入群成员
        for (GroupMemberDto dto : req.getMember()) {
            imGroupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), dto);
        }

        // 之后回调
        if (appConfig.isCreateGroupAfterCallback()) {

            callbackService.callback(req.getAppId(), CallbackCommand.CREATE_GROUP_AFTER,
                    JSON.toJSONString(imGroupEntity));
        }
    }

    @Override
    public GetGroupResp getGroupInfo(GetGroupReq req) {
        ImGroupEntity group = this.getGroup(req.getGroupId(), req.getAppId());

        if (group == null) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        GetGroupResp getGroupResp = new GetGroupResp();
        BeanUtils.copyProperties(group, getGroupResp);
        try {
            List<GroupMemberDto> groupMembers = imGroupMemberService
                    .getGroupMembers(req.getGroupId(), req.getAppId());
            getGroupResp.setMemberList(groupMembers);
        } catch (Exception e) {
            throw new ApplicationException(CommonErrorCode.SERVER_ERROR);
        }
        return getGroupResp;
    }

    @Override
    public GetJoinedGroupResp getJoinedGroup(GetJoinedGroupReq req) {
        List<String> joinedGroupIds = imGroupMemberService
                .getMemberJoinedGroupIds(req);

        List<ImGroupEntity> list;
        QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
        query.eq(DBColumn.APP_ID, req.getAppId());
        if (!CollUtil.isEmpty(req.getGroupType())) {
            query.in(DBColumn.GROUP_TYPE, req.getGroupType());
        }
        query.in(DBColumn.GROUP_ID, joinedGroupIds);
        list = imGroupMapper.selectList(query);

        GetJoinedGroupResp resp = new GetJoinedGroupResp();
        resp.setTotalCount(list.size());
        resp.setGroupList(list);
        return resp;
    }

    @Override
    @Transactional
    public void destroyGroup(DestroyGroupReq req) {
        boolean isAdmin = false;
        QueryWrapper<ImGroupEntity> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq(DBColumn.GROUP_ID, req.getGroupId());
        objectQueryWrapper.eq(DBColumn.APP_ID, req.getAppId());
        ImGroupEntity imGroupEntity = imGroupMapper.selectOne(objectQueryWrapper);
        if (imGroupEntity == null) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        if (!isAdmin) {
            // 根据腾讯云的设计，私有群只有管理员能解散
            if (imGroupEntity.getGroupType() == GroupTypeEnum.PRIVATE.getCode()) {
                throw new ApplicationException(GroupErrorCode.PRIVATE_GROUP_CAN_NOT_DESTROY);
            }

            if (imGroupEntity.getGroupType() == GroupTypeEnum.PUBLIC.getCode() &&
                    !imGroupEntity.getOwnerId().equals(req.getOperater())) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }

        ImGroupEntity update = new ImGroupEntity();
        update.setStatus(GroupStatusEnum.DESTROY.getCode());
        int update1 = imGroupMapper.update(update, objectQueryWrapper);
        if (update1 != 1) {
            throw new ApplicationException(GroupErrorCode.UPDATE_GROUP_BASE_INFO_ERROR);
        }

        // 之后回调
        if (appConfig.isModifyGroupAfterCallback()) {
            DestroyGroupCallbackDto dto = new DestroyGroupCallbackDto();
            dto.setGroupId(req.getGroupId());
            callbackService.callback(req.getAppId(), CallbackCommand.DESTROY_GROUP_AFTER,
                    JSON.toJSONString(dto));
        }
    }

    @Override
    @Transactional
    public void transferGroup(TransferGroupReq req) {

        GetRoleInGroupResp roleInGroupOne
                = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperater(), req.getAppId());

        if (roleInGroupOne == null) {
            throw new ApplicationException(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }

        if (roleInGroupOne.getRole() != GroupMemberRoleEnum.OWNER.getCode()) {
            throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
        }

        // 下一任群主在不在
        GetRoleInGroupResp newOwnerRole = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOwnerId(), req.getAppId());
        if (newOwnerRole == null) {
            throw new ApplicationException(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }

        ImGroupEntity updateGroup = new ImGroupEntity();
        updateGroup.setOwnerId(req.getOwnerId());
        UpdateWrapper<ImGroupEntity> updateGroupWrapper = new UpdateWrapper<>();
        updateGroupWrapper.eq(DBColumn.APP_ID, req.getAppId());
        updateGroupWrapper.eq(DBColumn.GROUP_ID, req.getGroupId());
        imGroupMapper.update(updateGroup, updateGroupWrapper);
        imGroupMemberService.transferGroupMember(req.getOwnerId(), req.getGroupId(), req.getAppId());
    }

    @Override
    public void muteGroup(MuteGroupReq req) {
        ImGroupEntity group = getGroup(req.getGroupId(), req.getAppId());
        if (group == null) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        boolean isAdmin = false;

        if (!isAdmin) {
            //不是后台调用需要检查权限
            GetRoleInGroupResp role = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperater(), req.getAppId());

            if (role == null) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_APP_MANAGER_ROLE);
            }

            Integer roleInfo = role.getRole();

            boolean isManager = roleInfo == GroupMemberRoleEnum.MANAGER.getCode()
                    || roleInfo == GroupMemberRoleEnum.OWNER.getCode();

            //公开群只能(群主/管理员)修改资料
            if (!isManager) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }
        }

        ImGroupEntity update = new ImGroupEntity();
        update.setMute(req.getMute());
        UpdateWrapper<ImGroupEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq(DBColumn.GROUP_ID, req.getGroupId());
        wrapper.eq(DBColumn.APP_ID, req.getAppId());
        imGroupMapper.update(update, wrapper);
    }
}
