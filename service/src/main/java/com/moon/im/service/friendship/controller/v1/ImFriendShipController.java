package com.moon.im.service.friendship.controller.v1;

import com.moon.im.common.ResponseVO;
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
        return friendService.importFriendShip(req);
    }

    @PostMapping("/addFriend")
    public ResponseVO<Object> addFriend(@RequestBody @Validated AddFriendReq req) {
        return friendService.addFriend(req);
    }

    @PostMapping("/updateFriend")
    public ResponseVO<Object> addFriend(@RequestBody @Validated UpdateFriendReq req) {
        return friendService.updateFriend(req);
    }

    @DeleteMapping("/deleteFriend")
    public ResponseVO<Object> deleteFriend(@RequestBody @Validated DeleteFriendReq req) {
        return friendService.deleteFriend(req);
    }

    @DeleteMapping("/deleteAllFriend")
    public ResponseVO<Object> deleteAllFriend(@RequestBody @Validated DeleteFriendReq req, Integer appId) {
        req.setAppId(appId);
        return friendService.deleteAllFriend(req);
    }

    @PostMapping("/getAllFriend")
    public ResponseVO<Object> getAllFriend(@RequestBody @Validated GetAllFriendShipReq req) {
        return friendService.getAllFriendShip(req);
    }

    @GetMapping("/getRelation")
    public ResponseVO<Object> getRelation(@RequestBody @Validated GetRelationReq req) {
        return friendService.getRelation(req);
    }

    @PostMapping("/checkFriendship")
    public ResponseVO<List<CheckFriendShipResp>> checkFriendship(@RequestBody @Validated CheckFriendShipReq req) {
        return friendService.checkFriendship(req);
    }

    @PostMapping("/addBlack")
    public ResponseVO<Object> addBlack(@RequestBody @Validated AddFriendShipBlackReq req) {
        return friendService.addBlack(req);
    }

    @DeleteMapping("/deleteBlack")
    public ResponseVO<Object> deleteBlack(@RequestBody @Validated DeleteBlackReq req) {
        return friendService.deleteBlack(req);
    }

    @PostMapping("/checkBlack")
    public ResponseVO<Object> checkBlack(@RequestBody @Validated CheckFriendShipReq req) {
        return friendService.checkBlack(req);
    }
}