package com.moon.im.service.friendship.controller.v1;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.friendship.dao.ImFriendShipEntity;
import com.moon.im.service.friendship.model.req.*;
import com.moon.im.service.friendship.model.resp.CheckFriendShipResp;
import com.moon.im.service.friendship.model.resp.ImportFriendShipResp;
import com.moon.im.service.friendship.service.ImFriendShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年01月29日
 */
@RestController
@RequestMapping("/friendship")
public class ImFriendShipController {

    @Autowired
    private ImFriendShipService friendService;

    @PostMapping("/importFriendShip")
    public ResponseVO<ImportFriendShipResp> importFriendShip(@RequestBody @Validated ImportFriendShipReq req) {
        return ResponseVO.successResponse(friendService.importFriendShip(req));
    }

    @PostMapping("/addFriend")
    public ResponseVO<Object> addFriend(@RequestBody @Validated AddFriendReq req) {
        friendService.addFriend(req);
        return ResponseVO.successResponse();
    }

    @PostMapping("/updateFriend")
    public ResponseVO<Object> addFriend(@RequestBody @Validated UpdateFriendReq req) {
        friendService.updateFriend(req);
        return ResponseVO.successResponse();
    }

    @DeleteMapping("/deleteFriend")
    public ResponseVO<Object> deleteFriend(@RequestBody @Validated DeleteFriendReq req) {
        friendService.deleteFriend(req);
        return ResponseVO.successResponse();
    }

    @DeleteMapping("/deleteAllFriend")
    public ResponseVO<Object> deleteAllFriend(@RequestBody @Validated DeleteFriendReq req) {
        friendService.deleteAllFriend(req);
        return ResponseVO.successResponse();
    }

    @PostMapping("/getAllFriend")
    public ResponseVO<List<ImFriendShipEntity>> getAllFriend(@RequestBody @Validated GetAllFriendShipReq req) {
        return ResponseVO.successResponse(friendService.getAllFriendShip(req));
    }

    @GetMapping("/getRelation")
    public ResponseVO<ImFriendShipEntity> getRelation(@RequestBody @Validated GetRelationReq req) {
        return ResponseVO.successResponse(friendService.getRelation(req));
    }

    @PostMapping("/checkFriendship")
    public ResponseVO<List<CheckFriendShipResp>> checkFriendship(@RequestBody @Validated CheckFriendShipReq req) {
        return ResponseVO.successResponse(friendService.checkFriendship(req));
    }

    @PostMapping("/addBlack")
    public ResponseVO<Object> addBlack(@RequestBody @Validated AddFriendShipBlackReq req) {
        friendService.addBlack(req);
        return ResponseVO.successResponse();
    }

    @DeleteMapping("/deleteBlack")
    public ResponseVO<Object> deleteBlack(@RequestBody @Validated DeleteBlackReq req) {
        friendService.deleteBlack(req);
        return ResponseVO.successResponse();
    }

    @PostMapping("/checkBlack")
    public ResponseVO<List<CheckFriendShipResp>> checkBlack(@RequestBody @Validated CheckFriendShipReq req) {
        return ResponseVO.successResponse(friendService.checkBlack(req));
    }
}