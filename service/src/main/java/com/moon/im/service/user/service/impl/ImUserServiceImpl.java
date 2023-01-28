package com.moon.im.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moon.im.common.ResponseVO;
import com.moon.im.common.enums.DelFlagEnum;
import com.moon.im.common.enums.UserErrorCode;
import com.moon.im.common.exception.ApplicationException;
import com.moon.im.service.user.dao.ImUserDataEntity;
import com.moon.im.service.user.dao.mapper.ImUserDataMapper;
import com.moon.im.service.user.model.req.*;
import com.moon.im.service.user.model.resp.GetUserInfoResp;
import com.moon.im.service.user.model.resp.ImportUserResp;
import com.moon.im.service.user.service.ImUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
@Service
public class ImUserServiceImpl implements ImUserService {

    @Autowired
    private ImUserDataMapper imUserDataMapper;

    @Override
    public ResponseVO<ImportUserResp> importUser(ImportUserReq req) {

        if (req.getUserData().size() > 100) {
            // TODO 返回数量过多
        }

        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();
        req.getUserData().forEach(e -> {
            try {

                e.setAppId(req.getAppId());

                int insert = imUserDataMapper.insert(e);
                if (insert == 1) {
                    successId.add(e.getUserId());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorId.add(e.getUserId());
            }
        });
        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<GetUserInfoResp> getUserInfo(GetUserInfoReq req) {
        QueryWrapper<ImUserDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id",req.getAppId());
        queryWrapper.in("user_id",req.getUserIds());
        queryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        List<ImUserDataEntity> userDataEntities = imUserDataMapper.selectList(queryWrapper);
        HashMap<String, ImUserDataEntity> map = new HashMap<>();

        for (ImUserDataEntity data:
                userDataEntities) {
            map.put(data.getUserId(),data);
        }

        List<String> failUser = new ArrayList<>();
        for (String uid:
                req.getUserIds()) {
            if(!map.containsKey(uid)){
                failUser.add(uid);
            }
        }

        GetUserInfoResp resp = new GetUserInfoResp();
        resp.setUserDataItem(userDataEntities);
        resp.setFailUser(failUser);
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<ImUserDataEntity> getSingleUserInfo(String userId, Integer appId) {
        QueryWrapper<ImUserDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id",appId);
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        ImUserDataEntity imUserDataEntity = imUserDataMapper.selectOne(queryWrapper);
        if(imUserDataEntity == null){
            return ResponseVO.errorResponse(UserErrorCode.USER_IS_NOT_EXIST);
        }

        return ResponseVO.successResponse(imUserDataEntity);
    }

    @Override
    public ResponseVO<Object> deleteUser(DeleteUserReq req) {
        ImUserDataEntity entity = new ImUserDataEntity();
        entity.setDelFlag(DelFlagEnum.DELETE.getCode());

        List<String> errorId = new ArrayList<>();
        List<String> successId = new ArrayList<>();

        for (String userId:
                req.getUserId()) {
            QueryWrapper<ImUserDataEntity> wrapper = new QueryWrapper();
            wrapper.eq("app_id",req.getAppId());
            wrapper.eq("user_id",userId);
            wrapper.eq("del_flag",DelFlagEnum.NORMAL.getCode());
            int update;

            try {
                update =  imUserDataMapper.update(entity, wrapper);
                if(update > 0){
                    successId.add(userId);
                }else{
                    errorId.add(userId);
                }
            }catch (Exception e){
                errorId.add(userId);
            }
        }

        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);
        return ResponseVO.successResponse(resp);
    }

    @Override
    @Transactional
    public ResponseVO modifyUserInfo(ModifyUserInfoReq req) {
//        QueryWrapper<ImUserDataEntity> query = new QueryWrapper<>();
//        query.eq("app_id",req.getAppId());
//        query.eq("user_id",req.getUserId());
//        query.eq("del_flag",DelFlagEnum.NORMAL.getCode());
//        ImUserDataEntity user = imUserDataMapper.selectOne(query);
//        if(user == null){
//            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
//        }
//
//        ImUserDataEntity update = new ImUserDataEntity();
//        BeanUtils.copyProperties(req,update);
//
//        update.setAppId(null);
//        update.setUserId(null);
//        int update1 = imUserDataMapper.update(update, query);
//        if(update1 == 1){
//            UserModifyPack pack = new UserModifyPack();
//            BeanUtils.copyProperties(req,pack);
//            messageProducer.sendToUser(req.getUserId(),req.getClientType(),req.getImei(),
//                    UserEventCommand.USER_MODIFY,pack,req.getAppId());
//
//            if(appConfig.isModifyUserAfterCallback()){
//                callbackService.callback(req.getAppId(),
//                        Constants.CallbackCommand.ModifyUserAfter,
//                        JSONObject.toJSONString(req));
//            }
//            return ResponseVO.successResponse();
//        }
//        throw new ApplicationException(UserErrorCode.MODIFY_USER_ERROR);
        return null;
    }

    @Override
    public ResponseVO<Object> login(LoginReq req) {
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<Object> getUserSequence(GetUserSequenceReq req) {
//        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(req.getAppId() + ":" + Constants.RedisConstants.SeqPrefix + ":" + req.getUserId());
//        Long groupSeq = imGroupService.getUserGroupMaxSeq(req.getUserId(),req.getAppId());
//        map.put(Constants.SeqConstants.Group,groupSeq);
//        return ResponseVO.successResponse(map);
        return null;
    }
}
