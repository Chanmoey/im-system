package com.moon.im.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moon.im.common.ResponseVO;
import com.moon.im.common.constant.DBColumn;
import com.moon.im.common.enums.DelFlagEnum;
import com.moon.im.common.enums.UserErrorCode;
import com.moon.im.common.exception.ApplicationException;
import com.moon.im.service.user.dao.ImUserDataEntity;
import com.moon.im.service.user.dao.mapper.ImUserDataMapper;
import com.moon.im.service.user.model.req.*;
import com.moon.im.service.user.model.resp.DeleteUserResp;
import com.moon.im.service.user.model.resp.GetUserInfoResp;
import com.moon.im.service.user.model.resp.ImportUserResp;
import com.moon.im.service.user.service.ImUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
@Service
public class ImUserServiceImpl implements ImUserService {

    @Autowired
    private ImUserDataMapper imUserDataMapper;

    @Override
    public ImportUserResp importUser(ImportUserReq req) {

        if (req.getUserData().size() > 100) {
            throw new ApplicationException(UserErrorCode.IMPORT_SIZE_BEYOND);
        }

        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();
        req.getUserData().forEach(e -> {
            try {
                e.setAppId(req.getAppId());
                int insert = imUserDataMapper.insert(e);
                if (insert == 1) {
                    successId.add(e.getUserId());
                } else {
                    errorId.add(e.getUserId());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorId.add(e.getUserId());
            }
        });
        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);
        return resp;
    }

    @Override
    public GetUserInfoResp getUserInfo(GetUserInfoReq req) {
        QueryWrapper<ImUserDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DBColumn.APP_ID, req.getAppId());
        queryWrapper.in(DBColumn.USER_ID, req.getUserIds());
        queryWrapper.eq(DBColumn.DEL_FLAG, DelFlagEnum.NORMAL.getCode());

        List<ImUserDataEntity> userDataEntities = imUserDataMapper.selectList(queryWrapper);
        HashMap<String, ImUserDataEntity> map = new HashMap<>();

        for (ImUserDataEntity data : userDataEntities) {
            map.put(data.getUserId(), data);
        }

        List<String> failUser = new ArrayList<>();
        for (String uid : req.getUserIds()) {
            if (!map.containsKey(uid)) {
                failUser.add(uid);
            }
        }

        GetUserInfoResp resp = new GetUserInfoResp();
        resp.setUserDataItem(userDataEntities);
        resp.setFailUser(failUser);
        return resp;
    }

    @Override
    public ImUserDataEntity getSingleUserInfo(String userId, Integer appId) {
        QueryWrapper<ImUserDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DBColumn.APP_ID, appId);
        queryWrapper.eq(DBColumn.USER_ID, userId);
        queryWrapper.eq(DBColumn.DEL_FLAG, DelFlagEnum.NORMAL.getCode());

        ImUserDataEntity imUserDataEntity = imUserDataMapper.selectOne(queryWrapper);
        if (imUserDataEntity == null) {
            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
        }
        return imUserDataEntity;
    }

    @Override
    public DeleteUserResp deleteUser(DeleteUserReq req) {
        ImUserDataEntity entity = new ImUserDataEntity();
        entity.setDelFlag(DelFlagEnum.DELETE.getCode());

        List<String> errorId = new ArrayList<>();
        List<String> successId = new ArrayList<>();

        for (String userId : req.getUserId()) {
            QueryWrapper<ImUserDataEntity> wrapper = new QueryWrapper<>();
            wrapper.eq(DBColumn.APP_ID, req.getAppId());
            wrapper.eq(DBColumn.USER_ID, userId);
            wrapper.eq(DBColumn.DEL_FLAG, DelFlagEnum.NORMAL.getCode());
            int update;
            try {
                update = imUserDataMapper.update(entity, wrapper);
                if (update > 0) {
                    successId.add(userId);
                } else {
                    errorId.add(userId);
                }
            } catch (Exception e) {
                errorId.add(userId);
            }
        }

        DeleteUserResp resp = new DeleteUserResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);
        return resp;
    }

    @Override
    public void login(LoginReq req) {

    }
}
