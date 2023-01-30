package com.moon.im.service.friendship.service;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.friendship.model.req.*;
import com.moon.im.service.friendship.model.resp.CheckFriendShipResp;
import com.moon.im.service.friendship.model.resp.ImportFriendShipResp;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年01月29日
 */
public interface ImFriendShipService {

    ResponseVO<ImportFriendShipResp> importFriendShip(ImportFriendShipReq req);

    ResponseVO<Object> addFriend(AddFriendReq req);

    ResponseVO<Object> updateFriend(UpdateFriendReq req);

    ResponseVO<Object> deleteFriend(DeleteFriendReq req);

    ResponseVO<Object> deleteAllFriend(DeleteFriendReq req);

    ResponseVO<Object> getAllFriendShip(GetAllFriendShipReq req);

    ResponseVO<Object> getRelation(GetRelationReq req);

    ResponseVO<List<CheckFriendShipResp>> checkFriendship(CheckFriendShipReq req);
}