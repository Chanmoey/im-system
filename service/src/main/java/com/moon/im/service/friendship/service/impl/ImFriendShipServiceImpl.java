package com.moon.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.moon.im.common.ResponseVO;
import com.moon.im.common.constant.DBColumn;
import com.moon.im.common.enums.FriendShipErrorCode;
import com.moon.im.common.enums.FriendShipStatusEnum;
import com.moon.im.common.enums.UserErrorCode;
import com.moon.im.common.exception.ApplicationException;
import com.moon.im.service.friendship.dao.ImFriendShipEntity;
import com.moon.im.service.friendship.dao.mapper.ImFriendShipMapper;
import com.moon.im.service.friendship.model.req.AddFriendReq;
import com.moon.im.service.friendship.model.req.FriendDto;
import com.moon.im.service.friendship.model.req.ImportFriendShipReq;
import com.moon.im.service.friendship.model.req.UpdateFriendReq;
import com.moon.im.service.friendship.model.resp.AddFriendResp;
import com.moon.im.service.friendship.model.resp.ImportFriendShipResp;
import com.moon.im.service.friendship.model.resp.UpdateFriendResp;
import com.moon.im.service.friendship.service.ImFriendShipService;
import com.moon.im.service.user.dao.ImUserDataEntity;
import com.moon.im.service.user.service.ImUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年01月29日
 */
@Service
public class ImFriendShipServiceImpl implements ImFriendShipService {

    @Autowired
    private ImFriendShipMapper imFriendShipMapper;

    @Autowired
    private ImUserService imUserService;

    @Override
    public ResponseVO<ImportFriendShipResp> importFriendShip(ImportFriendShipReq req) {
        if (req.getFriendItem().size() > 100) {
            throw new ApplicationException(FriendShipErrorCode.IMPORT_SIZE_BEYOND);
        }

        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();

        req.getFriendItem().forEach(item -> {
            ImFriendShipEntity entity = new ImFriendShipEntity();
            BeanUtils.copyProperties(item, entity);
            entity.setAppId(req.getAppId());
            entity.setFromId(req.getFromId());
            try {
                int insert = imFriendShipMapper.insert(entity);
                if (insert == 1) {
                    successId.add(item.getToId());
                } else {
                    errorId.add(item.getToId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorId.add(item.getToId());
            }
        });

        ImportFriendShipResp resp = new ImportFriendShipResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);

        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<AddFriendResp> addFriend(AddFriendReq req) {

        checkUserIsExit(req.getFromId(), req.getAppId());
        checkUserIsExit(req.getToItem().getToId(), req.getAppId());
        return doAddFriend(req.getFromId(), req.getToItem(), req.getAppId());
    }

    private ResponseVO<AddFriendResp> doAddFriend(String fromId, FriendDto dto, Integer appId) {
        // A添加B为好友
        // Friend表插入AB和BA两条记录
        // 查询是否有记录存在，如果存在则判断状态，如果是已添加、则提示已添加，如果为添加，则修改状态
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq(DBColumn.APP_ID, appId);
        query.eq(DBColumn.FROM_ID, fromId);
        query.eq(DBColumn.TO_ID, dto.getToId());
        ImFriendShipEntity entity = imFriendShipMapper.selectOne(query);

        if (entity == null) {
            // 添加
            entity = new ImFriendShipEntity();
            entity.setFromId(fromId);
            BeanUtils.copyProperties(dto, entity);
            entity.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NO_FRIEND.getCode());
            entity.setCreateTime(System.currentTimeMillis());
            int insert = imFriendShipMapper.insert(entity);
            if (insert != 1) {
                throw new ApplicationException(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            // 修改
            // 已经是好友
            if (entity.getStatus() == FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
                throw new ApplicationException(FriendShipErrorCode.ADD_FRIEND_ERROR);
            } else {
                // 不是好友
                ImFriendShipEntity update = new ImFriendShipEntity();
                if (!StringUtils.isBlank(dto.getAddSource())) {
                    update.setAddSource(dto.getAddSource());
                }

                if (!StringUtils.isBlank(dto.getRemark())) {
                    update.setRemark(dto.getRemark());
                }

                if (!StringUtils.isBlank(dto.getExtra())) {
                    update.setExtra(dto.getExtra());
                }

                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

                int result = imFriendShipMapper.update(update, query);
                if (result != 1) {
                    throw new ApplicationException(FriendShipErrorCode.ADD_FRIEND_ERROR);
                }
            }
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<UpdateFriendResp> updateFriend(UpdateFriendReq req) {
        checkUserIsExit(req.getFromId(), req.getAppId());
        checkUserIsExit(req.getToItem().getToId(), req.getAppId());
        return doUpdate(req.getFromId(), req.getToItem(), req.getAppId());
    }

    private ResponseVO<UpdateFriendResp> doUpdate(String fromId, FriendDto dto, Integer appId) {
        UpdateWrapper<ImFriendShipEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(ImFriendShipEntity::getAddSource, dto.getAddSource())
                .set(ImFriendShipEntity::getExtra, dto.getExtra())
                .set(ImFriendShipEntity::getRemark, dto.getRemark())
                .eq(ImFriendShipEntity::getAppId, appId)
                .eq(ImFriendShipEntity::getToId, dto.getToId())
                .eq(ImFriendShipEntity::getFromId, fromId);

        imFriendShipMapper.update(null, updateWrapper);

        return ResponseVO.successResponse();
    }

    private void checkUserIsExit(String userId, Integer appId) {
        ResponseVO<ImUserDataEntity> user = imUserService.getSingleUserInfo(userId, appId);

        if (!user.isOk()) {
            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
        }
    }
}