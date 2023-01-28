package com.moon.im.service.user.service;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.user.model.req.ImportUserReq;
import com.moon.im.service.user.model.resp.ImportUserResp;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
public interface ImUserService {

    public ResponseVO<ImportUserResp> importUser(ImportUserReq req);
}
