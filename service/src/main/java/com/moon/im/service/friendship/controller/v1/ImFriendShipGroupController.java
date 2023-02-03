package com.moon.im.service.friendship.controller.v1;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.friendship.model.req.AddFriendShipGroupMemberReq;
import com.moon.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.moon.im.service.friendship.model.req.DeleteFriendShipGroupMemberReq;
import com.moon.im.service.friendship.model.req.DeleteFriendShipGroupReq;
import com.moon.im.service.friendship.model.resp.AddFriendShipGroupMemberResp;
import com.moon.im.service.friendship.service.ImFriendShipGroupMemberService;
import com.moon.im.service.friendship.service.ImFriendShipGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/friendship/group")
public class ImFriendShipGroupController {

    @Autowired
    private ImFriendShipGroupService imFriendShipGroupService;

    @Autowired
    private ImFriendShipGroupMemberService imFriendShipGroupMemberService;


    @RequestMapping("/add")
    public ResponseVO<Object> add(@RequestBody @Validated AddFriendShipGroupReq req) {
        imFriendShipGroupService.addGroup(req);
        return ResponseVO.successResponse();
    }

    @RequestMapping("/del")
    public ResponseVO<Object> del(@RequestBody @Validated DeleteFriendShipGroupReq req) {
        imFriendShipGroupService.deleteGroup(req);
        return ResponseVO.successResponse();
    }

    @RequestMapping("/member/add")
    public ResponseVO<AddFriendShipGroupMemberResp> memberAdd(@RequestBody @Validated AddFriendShipGroupMemberReq req) {
        return ResponseVO.successResponse(
                imFriendShipGroupMemberService.addGroupMember(req)
        );
    }

    @RequestMapping("/member/del")
    public ResponseVO<Object> memberdel(@RequestBody @Validated DeleteFriendShipGroupMemberReq req) {
        imFriendShipGroupMemberService.delGroupMember(req);
        return ResponseVO.successResponse();
    }
}
