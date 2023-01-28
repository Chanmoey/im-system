package com.moon.im.service.friendship.service;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.friendship.model.req.ImportFriendShipReq;
import com.moon.im.service.friendship.model.req.ImportFriendShipResp;

/**
 * @author Chanmoey
 * @date 2023年01月29日
 */
public interface ImFriendService {

    public ResponseVO<ImportFriendShipResp> importFriendShip(ImportFriendShipReq req);
}
