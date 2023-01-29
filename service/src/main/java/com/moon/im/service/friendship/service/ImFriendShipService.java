package com.moon.im.service.friendship.service;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.friendship.model.req.AddFriendReq;
import com.moon.im.service.friendship.model.req.ImportFriendShipReq;
import com.moon.im.service.friendship.model.req.UpdateFriendReq;
import com.moon.im.service.friendship.model.resp.AddFriendResp;
import com.moon.im.service.friendship.model.resp.ImportFriendShipResp;
import com.moon.im.service.friendship.model.resp.UpdateFriendResp;

/**
 * @author Chanmoey
 * @date 2023年01月29日
 */
public interface ImFriendShipService {

    ResponseVO<ImportFriendShipResp> importFriendShip(ImportFriendShipReq req);

    ResponseVO<AddFriendResp> addFriend(AddFriendReq req);

    ResponseVO<UpdateFriendResp> updateFriend(UpdateFriendReq req);
}
