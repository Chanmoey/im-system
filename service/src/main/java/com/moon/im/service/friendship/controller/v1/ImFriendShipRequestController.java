package com.moon.im.service.friendship.controller.v1;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.friendship.model.req.*;
import com.moon.im.service.friendship.service.ImFriendShipRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Chanmoey
 * @date 2023年01月29日
 */
@RestController
@RequestMapping("/friendship")
public class ImFriendShipRequestController {

    @Autowired
    private ImFriendShipRequestService imFriendShipRequestService;

    @PostMapping("/approveFriendRequest")
    public ResponseVO<Object> approveFriendRequest(@RequestBody @Validated ApproveFriendRequestReq req) {
        return imFriendShipRequestService.approveFriendshipRequest(req);
    }
}