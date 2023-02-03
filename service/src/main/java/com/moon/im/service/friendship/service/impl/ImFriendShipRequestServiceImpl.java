package com.moon.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moon.im.common.constant.DBColumn;
import com.moon.im.common.enums.ApproverFriendRequestStatusEnum;
import com.moon.im.common.enums.FriendShipErrorCode;
import com.moon.im.common.enums.RequestFriendReadStatusEnum;
import com.moon.im.common.exception.ApplicationException;
import com.moon.im.service.friendship.dao.ImFriendShipRequestEntity;
import com.moon.im.service.friendship.dao.mapper.ImFriendShipRequestMapper;
import com.moon.im.service.friendship.model.req.ApproveFriendRequestReq;
import com.moon.im.service.friendship.model.req.FriendDto;
import com.moon.im.service.friendship.model.req.GetFriendShipRequestReq;
import com.moon.im.service.friendship.model.req.ReadFriendShipRequestReq;
import com.moon.im.service.friendship.service.ImFriendShipRequestService;
import com.moon.im.service.friendship.service.ImFriendShipService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年01月31日
 */
@Service
public class ImFriendShipRequestServiceImpl implements ImFriendShipRequestService {

    @Autowired
    private ImFriendShipRequestMapper imFriendShipRequestMapper;

    // TODO 解决循环依赖
    @Autowired
    private ImFriendShipService imFriendShipService;

    @Override
    public void addFriendshipRequest(String fromId, FriendDto dto, Integer appId) {

        QueryWrapper<ImFriendShipRequestEntity> query = new QueryWrapper<>();
        query.eq(DBColumn.APP_ID, appId);
        query.eq(DBColumn.FROM_ID, fromId);
        query.eq(DBColumn.TO_ID, dto.getToId());

        ImFriendShipRequestEntity request = imFriendShipRequestMapper.selectOne(query);

        if (request == null) {
            // 插入
            request = new ImFriendShipRequestEntity();
            request.setAddSource(dto.getAddSource());
            request.setAddWording(dto.getAddWording());
            request.setAppId(appId);
            request.setFromId(fromId);
            request.setToId(dto.getToId());
            request.setReadStatus(RequestFriendReadStatusEnum.UNREAD.getCode());
            request.setApproveStatus(ApproverFriendRequestStatusEnum.UNAUDITED.getCode());
            request.setRemark(dto.getRemark());
            request.setCreateTime(System.currentTimeMillis());
            try {
                int insert = imFriendShipRequestMapper.insert(request);
                if (insert != 1) {
                    throw new ApplicationException(FriendShipErrorCode.ADD_FRIEND_ERROR);
                }
            } catch (Exception e) {
                throw new ApplicationException(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            // 修改记录内容和更新时间
            if (StringUtils.isNotBlank(dto.getAddSource())) {
                request.setAddSource(dto.getAddSource());
            }

            if (StringUtils.isNotBlank(dto.getRemark())) {
                request.setRemark(dto.getRemark());
            }

            if (StringUtils.isNotBlank(dto.getAddWording())) {
                request.setAddWording(dto.getAddWording());
            }

            try {
                int update = imFriendShipRequestMapper.updateById(request);
                if (update != 1) {
                    throw new ApplicationException(FriendShipErrorCode.ADD_FRIEND_ERROR);
                }
            } catch (Exception e) {
                throw new ApplicationException(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        }
    }

    @Override
    @Transactional
    public void approveFriendshipRequest(ApproveFriendRequestReq req) {

        ImFriendShipRequestEntity imFriendShipRequestEntity = imFriendShipRequestMapper.selectById(req.getId());
        if (imFriendShipRequestEntity == null) {
            throw new ApplicationException(FriendShipErrorCode.FRIEND_REQUEST_IS_NOT_EXIST);
        }

        if (!req.getOperater().equals(imFriendShipRequestEntity.getToId())) {
            //只能审批发给自己的好友请求
            throw new ApplicationException(FriendShipErrorCode.NOT_APPROVE_OTHER_MAN_REQUEST);
        }

        ImFriendShipRequestEntity update = new ImFriendShipRequestEntity();
        update.setApproveStatus(req.getStatus());
        update.setUpdateTime(System.currentTimeMillis());
        update.setId(req.getId());
        imFriendShipRequestMapper.updateById(update);

        //TODO 使用异步线程池解决循环依赖问题
        if (ApproverFriendRequestStatusEnum.AGREE.getCode() == req.getStatus()) {
            //同意 ===> 去执行添加好友逻辑
            FriendDto dto = new FriendDto();
            dto.setAddSource(imFriendShipRequestEntity.getAddSource());
            dto.setAddWording(imFriendShipRequestEntity.getAddWording());
            dto.setRemark(imFriendShipRequestEntity.getRemark());
            dto.setToId(imFriendShipRequestEntity.getToId());
            imFriendShipService.doAddFriend(imFriendShipRequestEntity.getFromId(), dto, req.getAppId());
        }
    }

    @Override
    public void readFriendShipRequest(ReadFriendShipRequestReq req) {
        QueryWrapper<ImFriendShipRequestEntity> query = new QueryWrapper<>();
        query.eq(DBColumn.APP_ID, req.getAppId());

        // 审批发给我的好友申请，req中的fromId是我
        query.eq(DBColumn.TO_ID, req.getFromId());

        ImFriendShipRequestEntity update = new ImFriendShipRequestEntity();
        update.setReadStatus(RequestFriendReadStatusEnum.READ.getCode());
        imFriendShipRequestMapper.update(update, query);
    }

    @Override
    public List<ImFriendShipRequestEntity> getFriendShipRequest(GetFriendShipRequestReq req) {
        QueryWrapper<ImFriendShipRequestEntity> query = new QueryWrapper<>();
        query.eq(DBColumn.APP_ID, req.getAppId());

        // 审批发给我的好友申请，req中的fromId是我
        query.eq(DBColumn.TO_ID, req.getFromId());

        return imFriendShipRequestMapper.selectList(query);
    }
}
