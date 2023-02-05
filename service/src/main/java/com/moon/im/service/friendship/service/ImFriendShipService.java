package com.moon.im.service.friendship.service;

import com.moon.im.common.ResponseVO;
import com.moon.im.common.model.RequestBase;
import com.moon.im.service.friendship.dao.ImFriendShipEntity;
import com.moon.im.service.friendship.model.req.*;
import com.moon.im.service.friendship.model.resp.CheckFriendShipResp;
import com.moon.im.service.friendship.model.resp.ImportFriendShipResp;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年01月29日
 */
public interface ImFriendShipService {

    ImportFriendShipResp importFriendShip(ImportFriendShipReq req);

    void addFriend(AddFriendReq req);

    void doAddFriend(RequestBase requestBase, String fromId, FriendDto dto, Integer appId);

    void updateFriend(UpdateFriendReq req);

    void deleteFriend(DeleteFriendReq req);

    void deleteAllFriend(DeleteFriendReq req);

    List<ImFriendShipEntity> getAllFriendShip(GetAllFriendShipReq req);

    ImFriendShipEntity getRelation(GetRelationReq req);

    ImFriendShipEntity getRelation(String fromId, String toId, Integer appId);

    List<CheckFriendShipResp> checkFriendship(CheckFriendShipReq req);


    void addBlack(AddFriendShipBlackReq req);

    void deleteBlack(DeleteBlackReq req);

    List<CheckFriendShipResp> checkBlack(CheckFriendShipReq req);
}