package com.moon.im.service.friendship.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.moon.im.codec.pack.friendship.*;
import com.moon.im.common.ResponseVO;
import com.moon.im.common.config.AppConfig;
import com.moon.im.common.constant.CallbackCommand;
import com.moon.im.common.constant.DBColumn;
import com.moon.im.common.enums.AllowFriendTypeEnum;
import com.moon.im.common.enums.CheckFriendShipTypeEnum;
import com.moon.im.common.enums.FriendShipErrorCode;
import com.moon.im.common.enums.FriendShipStatusEnum;
import com.moon.im.common.enums.command.FriendshipEventCommand;
import com.moon.im.common.exception.ApplicationException;
import com.moon.im.common.model.RequestBase;
import com.moon.im.service.friendship.dao.ImFriendShipEntity;
import com.moon.im.service.friendship.dao.mapper.ImFriendShipMapper;
import com.moon.im.service.friendship.model.callback.AddFriendAfterCallbackDto;
import com.moon.im.service.friendship.model.callback.AddFriendBlackAfterCallbackDto;
import com.moon.im.service.friendship.model.callback.DeleteFriendAfterCallbackDto;
import com.moon.im.service.friendship.model.req.*;
import com.moon.im.service.friendship.model.resp.CheckFriendShipResp;
import com.moon.im.service.friendship.model.resp.ImportFriendShipResp;
import com.moon.im.service.friendship.service.ImFriendShipRequestService;
import com.moon.im.service.friendship.service.ImFriendShipService;
import com.moon.im.service.user.dao.ImUserDataEntity;
import com.moon.im.service.user.service.ImUserService;
import com.moon.im.service.util.CallbackService;
import com.moon.im.service.util.MessageProducer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Autowired
    private ImFriendShipRequestService imFriendShipRequestService;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private CallbackService callbackService;

    @Autowired
    private MessageProducer messageProducer;

    @Override
    public ImportFriendShipResp importFriendShip(ImportFriendShipReq req) {

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

        return resp;
    }

    @Override
    public void addFriend(AddFriendReq req) {

        checkUserIsExit(req.getFromId(), req.getAppId());
        ImUserDataEntity toUser = checkUserIsExit(req.getToItem().getToId(), req.getAppId());

        // 之前回调
        if (appConfig.isAddFriendBeforeCallback()) {
            ResponseVO<Object> responseVO = callbackService.beforeCallback(req.getAppId(), CallbackCommand.ADD_FRIEND_BEFORE,
                    JSON.toJSONString(req));

            if (!responseVO.isOk()) {
                throw new ApplicationException(responseVO.getCode(), responseVO.getMsg());
            }
        }


        // 对方添加好友的方式为：无需验证。
        if (toUser.getFriendAllowType() != null
                && toUser.getFriendAllowType() == AllowFriendTypeEnum.NOT_NEED.getCode()) {
            doAddFriend(req, req.getFromId(), req.getToItem(), req.getAppId());
        } else {
            // 申请流程
            // 插入一条好友申请的记录
            imFriendShipRequestService.addFriendshipRequest(req.getFromId(), req.getToItem(), req.getAppId());
        }
    }

    public void doAddFriend(RequestBase requestBase, String fromId, FriendDto dto, Integer appId) {
        // A添加B为好友
        // Friend表插入AB和BA两条记录
        // 查询是否有记录存在，如果存在则判断状态，如果是已添加、则提示已添加，如果为添加，则修改状态
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq(DBColumn.APP_ID, appId);
        query.eq(DBColumn.FROM_ID, fromId);
        query.eq(DBColumn.TO_ID, dto.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);

        if (fromItem == null) {
            // 添加
            fromItem = new ImFriendShipEntity();
            fromItem.setFromId(fromId);
            BeanUtils.copyProperties(dto, fromItem);
            fromItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NO_FRIEND.getCode());
            fromItem.setCreateTime(System.currentTimeMillis());
            int insert = imFriendShipMapper.insert(fromItem);
            if (insert != 1) {
                throw new ApplicationException(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            // 修改
            // 已经是好友
            if (fromItem.getStatus() == FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
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

                try {
                    int result = imFriendShipMapper.update(update, query);
                    if (result != 1) {
                        throw new ApplicationException(FriendShipErrorCode.ADD_FRIEND_ERROR);
                    }
                } catch (Exception e) {
                    throw new ApplicationException(FriendShipErrorCode.ADD_FRIEND_ERROR);
                }
            }
        }

        // B -> A方向也插入一条数据
        QueryWrapper<ImFriendShipEntity> toQuery = new QueryWrapper<>();
        toQuery.eq(DBColumn.APP_ID, appId);
        toQuery.eq(DBColumn.FROM_ID, dto.getToId());
        toQuery.eq(DBColumn.TO_ID, fromId);
        ImFriendShipEntity toItem = imFriendShipMapper.selectOne(toQuery);

        if (toItem == null) {
            toItem = new ImFriendShipEntity();
            BeanUtils.copyProperties(dto, toItem);
            toItem.setAppId(appId);
            toItem.setFromId(dto.getToId());
            toItem.setToId(fromId);
            toItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NO_FRIEND.getCode());
            toItem.setCreateTime(System.currentTimeMillis());
            toItem.setBlack(FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
            try {
                int insert = imFriendShipMapper.insert(toItem);
                if (insert != 1) {
                    throw new ApplicationException(FriendShipErrorCode.ADD_FRIEND_ERROR);
                }
            } catch (Exception e) {
                throw new ApplicationException(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        }

        // A添加B，需要发送给A的其他端，发送给B的所有端
        AddFriendPack addFriendPack = new AddFriendPack();
        BeanUtils.copyProperties(fromId, addFriendPack);
        if (requestBase != null) {
            messageProducer.sendToUser(fromId, requestBase.getClientType(), requestBase.getImei(),
                    FriendshipEventCommand.FRIEND_ADD, addFriendPack, appId);
        } else {
            messageProducer.sendToUser(fromId, FriendshipEventCommand.FRIEND_ADD,
                    addFriendPack, appId);
        }

        // 发送给B
        AddFriendPack addFriendToPack = new AddFriendPack();
        BeanUtils.copyProperties(toItem, addFriendToPack);
        messageProducer.sendToUser(toItem.getFromId(), FriendshipEventCommand.FRIEND_ADD,
                addFriendToPack, appId);

        // 之后回调
        if (appConfig.isAddFriendAfterCallback()) {

            AddFriendAfterCallbackDto callbackDto = new AddFriendAfterCallbackDto();
            callbackDto.setFromId(fromId);
            callbackDto.setToItem(dto);

            callbackService.callback(appId, CallbackCommand.ADD_FRIEND_AFTER,
                    JSON.toJSONString(callbackDto));
        }
    }

    @Override
    public void updateFriend(UpdateFriendReq req) {
        checkUserIsExit(req.getFromId(), req.getAppId());
        checkUserIsExit(req.getToItem().getToId(), req.getAppId());
        doUpdate(req.getFromId(), req.getToItem(), req.getAppId());

        // 发送TCP通知
        UpdateFriendPack updateFriendPack = new UpdateFriendPack();
        updateFriendPack.setRemark(req.getToItem().getRemark());
        updateFriendPack.setToId(req.getToItem().getToId());
        messageProducer.sendToUser(req.getFromId(), req.getClientType(),
                req.getImei(), FriendshipEventCommand.FRIEND_UPDATE, updateFriendPack, req.getAppId());

        if (appConfig.isModifyFriendAfterCallback()) {
            AddFriendAfterCallbackDto callbackDto = new AddFriendAfterCallbackDto();
            callbackDto.setFromId(req.getFromId());
            callbackDto.setToItem(req.getToItem());
            callbackService.callback(req.getAppId(), CallbackCommand.UPDATE_FRIEND_AFTER,
                    JSON.toJSONString(callbackDto));
        }
    }

    private void doUpdate(String fromId, FriendDto dto, Integer appId) {
        UpdateWrapper<ImFriendShipEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(ImFriendShipEntity::getAddSource, dto.getAddSource())
                .set(ImFriendShipEntity::getExtra, dto.getExtra())
                .set(ImFriendShipEntity::getRemark, dto.getRemark())
                .eq(ImFriendShipEntity::getAppId, appId)
                .eq(ImFriendShipEntity::getToId, dto.getToId())
                .eq(ImFriendShipEntity::getFromId, fromId);

        int update = imFriendShipMapper.update(null, updateWrapper);

        if (update != 1) {
            throw new ApplicationException(FriendShipErrorCode.UPDATE_FRIEND_ERROR);
        }
    }

    @Override
    public void deleteFriend(DeleteFriendReq req) {
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq(DBColumn.APP_ID, req.getAppId());
        query.eq(DBColumn.FROM_ID, req.getFromId());
        query.eq(DBColumn.TO_ID, req.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);

        if (fromItem == null) {
            throw new ApplicationException(FriendShipErrorCode.TO_IS_NOT_YOUR_FRIEND);
        }

        if (fromItem.getStatus() == FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
            // 删除
            ImFriendShipEntity update = new ImFriendShipEntity();
            update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
            imFriendShipMapper.update(update, query);
        } else {
            throw new ApplicationException(FriendShipErrorCode.FRIEND_IS_DELETED);
        }

        // 发送TCP通知
        DeleteFriendPack deleteFriendPack = new DeleteFriendPack();
        deleteFriendPack.setFromId(req.getFromId());
        deleteFriendPack.setToId(req.getToId());
        messageProducer.sendToUser(req.getFromId(), req.getClientType(),
                req.getImei(), FriendshipEventCommand.FRIEND_DELETE,
                deleteFriendPack, req.getAppId());

        // 之后回调
        if (appConfig.isDeleteFriendAfterCallback()) {

            DeleteFriendAfterCallbackDto callbackDto = new DeleteFriendAfterCallbackDto();
            callbackDto.setFromId(req.getFromId());
            callbackDto.setToId(req.getToId());

            callbackService.callback(req.getAppId(), CallbackCommand.DELETE_FRIEND_AFTER,
                    JSON.toJSONString(callbackDto));
        }
    }

    @Override
    public void deleteAllFriend(DeleteFriendReq req) {
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq(DBColumn.APP_ID, req.getAppId());
        query.eq(DBColumn.FROM_ID, req.getFromId());
        query.eq(DBColumn.FS_STATUS, FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

        ImFriendShipEntity update = new ImFriendShipEntity();
        update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        imFriendShipMapper.update(update, query);

        // 发送TCP通知
        DeleteAllFriendPack deleteAllFriendPack = new DeleteAllFriendPack();
        deleteAllFriendPack.setFromId(req.getFromId());
        messageProducer.sendToUser(req.getFromId(), req.getClientType(),
                req.getImei(), FriendshipEventCommand.FRIEND_DELETE,
                deleteAllFriendPack, req.getAppId());
    }

    @Override
    public List<ImFriendShipEntity> getAllFriendShip(GetAllFriendShipReq req) {
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq(DBColumn.APP_ID, req.getAppId());
        query.eq(DBColumn.FROM_ID, req.getFromId());
        return imFriendShipMapper.selectList(query);
    }

    @Override
    public ImFriendShipEntity getRelation(GetRelationReq req) {

        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq(DBColumn.APP_ID, req.getAppId());
        query.eq(DBColumn.FROM_ID, req.getFromId());
        query.eq(DBColumn.TO_ID, req.getToId());

        ImFriendShipEntity entity = imFriendShipMapper.selectOne(query);

        if (entity == null) {
            throw new ApplicationException(FriendShipErrorCode.RELATION_IS_NOT_EXIST);
        }
        return entity;
    }

    @Override
    public ImFriendShipEntity getRelation(String fromId, String toId, Integer appId) {

        GetRelationReq req = new GetRelationReq();
        req.setFromId(fromId);
        req.setToId(toId);
        req.setAppId(appId);

        return getRelation(req);
    }

    @Override
    public List<CheckFriendShipResp> checkFriendship(CheckFriendShipReq req) {

        Map<String, Integer> result = req.getToIds().stream()
                .collect(Collectors.toMap(Function.identity(), s -> 0));

        List<CheckFriendShipResp> resp;
        if (req.getCheckType() == CheckFriendShipTypeEnum.SINGLE.getType()) {
            resp = imFriendShipMapper.checkFriendShip(req);
        } else {
            resp = imFriendShipMapper.checkFriendShipBoth(req);
        }

        Map<String, Integer> collect = resp.stream()
                .collect(Collectors.toMap(CheckFriendShipResp::getToId, CheckFriendShipResp::getStatus));

        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            if (!collect.containsKey(entry.getKey())) {
                CheckFriendShipResp noFriend = new CheckFriendShipResp();
                noFriend.setFromId(req.getFromId());
                noFriend.setToId(entry.getKey());
                noFriend.setStatus(entry.getValue());
                resp.add(noFriend);
            }
        }
        return resp;
    }

    @Override
    public void addBlack(AddFriendShipBlackReq req) {
        imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        imUserService.getSingleUserInfo(req.getToId(), req.getAppId());

        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq(DBColumn.APP_ID, req.getAppId());
        query.eq(DBColumn.FROM_ID, req.getFromId());
        query.eq(DBColumn.TO_ID, req.getToId());

        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);
        if (fromItem == null) {

            fromItem = new ImFriendShipEntity();
            fromItem.setFromId(req.getFromId());
            fromItem.setToId(req.getToId());
            fromItem.setAppId(req.getAppId());
            fromItem.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
            fromItem.setCreateTime(System.currentTimeMillis());
            int insert = imFriendShipMapper.insert(fromItem);
            if (insert != 1) {
                throw new ApplicationException(FriendShipErrorCode.ADD_BLACK_ERROR);
            }
        } else {
            //如果存在则判断状态，如果是拉黑，则提示已拉黑，如果是未拉黑，则修改状态
            if (fromItem.getBlack() != null && fromItem.getBlack() == FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode()) {
                throw new ApplicationException(FriendShipErrorCode.FRIEND_IS_BLACK);
            } else {
                ImFriendShipEntity update = new ImFriendShipEntity();
                update.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
                int result = imFriendShipMapper.update(update, query);
                if (result != 1) {
                    throw new ApplicationException(FriendShipErrorCode.ADD_BLACK_ERROR);
                }
            }
        }

        // 发送TCP通知
        AddFriendBlackPack addFriendBlackPack = new AddFriendBlackPack();
        addFriendBlackPack.setFromId(req.getFromId());
        addFriendBlackPack.setToId(req.getToId());
        messageProducer.sendToUser(req.getFromId(), req.getClientType(),
                req.getImei(), FriendshipEventCommand.FRIEND_BLACK_ADD,
                addFriendBlackPack, req.getAppId());

        // 之后回调
        if (appConfig.isAddFriendShipBlackAfterCallback()) {

            AddFriendBlackAfterCallbackDto callbackDto = new AddFriendBlackAfterCallbackDto();
            callbackDto.setFromId(req.getFromId());
            callbackDto.setToId(req.getToId());

            callbackService.callback(req.getAppId(), CallbackCommand.ADD_BLACK_AFTER,
                    JSON.toJSONString(callbackDto));
        }
    }

    @Override
    public void deleteBlack(DeleteBlackReq req) {
        QueryWrapper<ImFriendShipEntity> queryFrom = new QueryWrapper<ImFriendShipEntity>()
                .eq(DBColumn.FROM_ID, req.getFromId())
                .eq(DBColumn.APP_ID, req.getAppId())
                .eq(DBColumn.TO_ID, req.getToId());

        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(queryFrom);
        if (fromItem.getBlack() != null && fromItem.getBlack() != FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode()) {
            throw new ApplicationException(FriendShipErrorCode.FRIEND_IS_NOT_YOUR_BLACK);
        }

        ImFriendShipEntity update = new ImFriendShipEntity();
        update.setBlack(FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
        int update1 = imFriendShipMapper.update(update, queryFrom);

        if (update1 != 1) {
            throw new ApplicationException(FriendShipErrorCode.UPDATE_FRIEND_ERROR);
        }

        // 发送TCP通知
        DeleteBlackPack deleteBlackPack = new DeleteBlackPack();
        deleteBlackPack.setFromId(req.getFromId());
        deleteBlackPack.setToId(req.getToId());
        messageProducer.sendToUser(req.getFromId(), req.getClientType(),
                req.getImei(), FriendshipEventCommand.FRIEND_BLACK_DELETE,
                deleteBlackPack, req.getAppId());

        // 之后回调
        if (appConfig.isDeleteFriendShipBlackAfterCallback()) {

            AddFriendBlackAfterCallbackDto callbackDto = new AddFriendBlackAfterCallbackDto();
            callbackDto.setFromId(req.getFromId());
            callbackDto.setToId(req.getToId());

            callbackService.callback(req.getAppId(), CallbackCommand.DELETE_BLACK,
                    JSON.toJSONString(callbackDto));
        }
    }

    @Override
    public List<CheckFriendShipResp> checkBlack(CheckFriendShipReq req) {

        Map<String, Integer> toIdMap
                = req.getToIds().stream().collect(Collectors
                .toMap(Function.identity(), s -> 0));
        List<CheckFriendShipResp> result;
        if (req.getCheckType() == CheckFriendShipTypeEnum.SINGLE.getType()) {
            result = imFriendShipMapper.checkFriendShipBlack(req);
        } else {
            result = imFriendShipMapper.checkFriendShipBlackBoth(req);
        }

        Map<String, Integer> collect = result.stream()
                .collect(Collectors
                        .toMap(CheckFriendShipResp::getToId,
                                CheckFriendShipResp::getStatus));
        for (Map.Entry<String, Integer> entry :
                toIdMap.entrySet()) {
            if (!collect.containsKey(entry.getKey())) {
                CheckFriendShipResp checkFriendShipResp = new CheckFriendShipResp();
                checkFriendShipResp.setToId(entry.getKey());
                checkFriendShipResp.setFromId(req.getFromId());
                checkFriendShipResp.setStatus(entry.getValue());
                result.add(checkFriendShipResp);
            }
        }

        return result;
    }

    private ImUserDataEntity checkUserIsExit(String userId, Integer appId) {
        return imUserService.getSingleUserInfo(userId, appId);
    }
}
