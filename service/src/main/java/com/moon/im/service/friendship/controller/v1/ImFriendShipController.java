package com.moon.im.service.friendship.controller.v1;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.friendship.model.req.AddFriendReq;
import com.moon.im.service.friendship.model.req.ImportFriendShipReq;
import com.moon.im.service.friendship.model.req.UpdateFriendReq;
import com.moon.im.service.friendship.model.resp.AddFriendResp;
import com.moon.im.service.friendship.model.resp.ImportFriendShipResp;
import com.moon.im.service.friendship.model.resp.UpdateFriendResp;
import com.moon.im.service.friendship.service.ImFriendShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
