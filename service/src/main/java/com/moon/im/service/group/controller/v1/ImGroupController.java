package com.moon.im.service.group.controller.v1;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.group.model.req.*;
import com.moon.im.service.group.model.resp.GetGroupResp;
import com.moon.im.service.group.model.resp.GetJoinedGroupResp;
import com.moon.im.service.group.service.ImGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/group")
public class ImGroupController {

    @Autowired
    ImGroupService groupService;

    @RequestMapping("/importGroup")
    public ResponseVO<Object> importGroup(@RequestBody @Validated ImportGroupReq req) {
        groupService.importGroup(req);
        return ResponseVO.successResponse();
    }

    @RequestMapping("/createGroup")
    public ResponseVO<Object> createGroup(@RequestBody @Validated CreateGroupReq req) {
        groupService.createGroup(req);
        return ResponseVO.successResponse();
    }

    @RequestMapping("/update")
    public ResponseVO<Object> update(@RequestBody @Validated UpdateGroupReq req) {
        groupService.updateGroupInfo(req);
        return ResponseVO.successResponse();
    }

    @RequestMapping("/getGroupInfo")
    public ResponseVO<GetGroupResp> getGroupInfo(@RequestBody @Validated GetGroupReq req) {
        return ResponseVO.successResponse(groupService.getGroupInfo(req));
    }

    @RequestMapping("/getJoinedGroup")
    public ResponseVO<GetJoinedGroupResp> getJoinedGroup(@RequestBody @Validated GetJoinedGroupReq req) {
        return ResponseVO.successResponse(groupService.getJoinedGroup(req));
    }

    @RequestMapping("/destroyGroup")
    public ResponseVO<Object> destroyGroup(@RequestBody @Validated DestroyGroupReq req) {
        groupService.destroyGroup(req);
        return ResponseVO.successResponse();
    }

    @RequestMapping("/transferGroup")
    public ResponseVO<Object> transferGroup(@RequestBody @Validated TransferGroupReq req) {
        groupService.transferGroup(req);
        return ResponseVO.successResponse();
    }

    @RequestMapping("/forbidSendMessage")
    public ResponseVO<Object> forbidSendMessage(@RequestBody @Validated MuteGroupReq req) {
        groupService.muteGroup(req);
        return ResponseVO.successResponse();
    }
//
//    @RequestMapping("/sendMessage")
//    public ResponseVO sendMessage(@RequestBody @Validated SendGroupMessageReq
//                                          req, Integer appId,
//                                  String identifier)  {
//        req.setAppId(appId);
//        req.setOperater(identifier);
//        return ResponseVO.successResponse(groupMessageService.send(req));
//    }
//
//    @RequestMapping("/syncJoinedGroup")
//    public ResponseVO syncJoinedGroup(@RequestBody @Validated SyncReq req, Integer appId, String identifier)  {
//        req.setAppId(appId);
//        return groupService.syncJoinedGroupList(req);
//    }
}
