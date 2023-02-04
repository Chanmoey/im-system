package com.moon.im.service.user.service;

import com.moon.im.service.user.dao.ImUserDataEntity;
import com.moon.im.service.user.model.req.*;
import com.moon.im.service.user.model.resp.DeleteUserResp;
import com.moon.im.service.user.model.resp.GetUserInfoResp;
import com.moon.im.service.user.model.resp.ImportUserResp;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
public interface ImUserService {

    ImportUserResp importUser(ImportUserReq req);

    GetUserInfoResp getUserInfo(GetUserInfoReq req);

    ImUserDataEntity getSingleUserInfo(String userId, Integer appId);

    DeleteUserResp deleteUser(DeleteUserReq req);

    void modifyUserInfo(ModifyUserInfoReq req);

    void login(LoginReq req);
}
