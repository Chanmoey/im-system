package com.moon.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moon.im.common.ResponseVO;
import com.moon.im.common.constant.DBColumn;
import com.moon.im.common.enums.ApproverFriendRequestStatusEnum;
import com.moon.im.common.enums.FriendShipErrorCode;
import com.moon.im.common.enums.RequestFriendReadStatusEnum;
import com.moon.im.common.exception.ApplicationException;
import com.moon.im.service.friendship.dao.ImFriendShipRequestEntity;
import com.moon.im.service.friendship.dao.mapper.ImFriendShipRequestMapper;
import com.moon.im.service.friendship.model.req.FriendDto;
import com.moon.im.service.friendship.service.ImFriendShipRequestService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Chanmoey
 * @date 2023年01月31日
 */
@Service
public class ImFriendShipRequestServiceImpl implements ImFriendShipRequestService {

    @Autowired
    private ImFriendShipRequestMapper imFriendShipRequestMapper;

    @Override
    public ResponseVO<Object> addFriendshipRequest(String fromId, FriendDto dto, Integer appId) {

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

        return ResponseVO.successResponse();
    }
}
