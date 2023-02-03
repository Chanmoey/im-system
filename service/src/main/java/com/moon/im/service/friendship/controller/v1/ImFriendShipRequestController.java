package com.moon.im.service.friendship.controller.v1;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.friendship.model.req.ApproveFriendRequestReq;
import com.moon.im.service.friendship.service.ImFriendShipRequestService;
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
public class ImFriendShipRequestController {

    @Autowired
    private ImFriendShipRequestService imFriendShipRequestService;

    @PostMapping("/approveFriendRequest")
    public ResponseVO<Object> approveFriendRequest(@RequestBody @Validated ApproveFriendRequestReq req) {
        imFriendShipRequestService.approveFriendshipRequest(req);
        return ResponseVO.successResponse();
    }
}