package com.moon.im.service.friendship.controller.v1;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.friendship.model.req.*;
import com.moon.im.service.friendship.model.resp.AddFriendResp;
import com.moon.im.service.friendship.model.resp.ImportFriendShipResp;
import com.moon.im.service.friendship.model.resp.UpdateFriendResp;
import com.moon.im.service.friendship.service.ImFriendShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseVO<AddFriendResp> addFriend(@RequestBody @Validated AddFriendReq req) {
        return friendService.addFriend(req);
    }

    @PostMapping("/updateFriend")
    public ResponseVO<UpdateFriendResp> addFriend(@RequestBody @Validated UpdateFriendReq req) {
        return friendService.updateFriend(req);
    }

    @PostMapping("/deleteFriend")
    public ResponseVO<Object> deleteFriend(@RequestBody @Validated DeleteFriendReq req) {
        return friendService.deleteFriend(req);
    }

    @PostMapping("/getAllFriend")
    public ResponseVO<Object> getAllFriend(@RequestBody @Validated GetAllFriendShipReq req) {
        return friendService.getAllFriendShip(req);
    }

    @GetMapping("/getRelation")
    public ResponseVO<Object> getRelation(@RequestBody @Validated GetRelationReq req) {
        return friendService.getRelation(req);
    }
}
