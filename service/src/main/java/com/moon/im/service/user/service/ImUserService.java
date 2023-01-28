package com.moon.im.service.user.service;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.user.dao.ImUserDataEntity;
import com.moon.im.service.user.model.req.*;
import com.moon.im.service.user.model.resp.GetUserInfoResp;
import com.moon.im.service.user.model.resp.ImportUserResp;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
public interface ImUserService {

    ResponseVO<ImportUserResp> importUser(ImportUserReq req);

    public ResponseVO<GetUserInfoResp> getUserInfo(GetUserInfoReq req);

    public ResponseVO<ImUserDataEntity> getSingleUserInfo(String userId , Integer appId);

    public ResponseVO<Object> deleteUser(DeleteUserReq req);

    public ResponseVO<Object> modifyUserInfo(ModifyUserInfoReq req);

    public ResponseVO<Object> login(LoginReq req);

    ResponseVO<Object> getUserSequence(GetUserSequenceReq req);
}
