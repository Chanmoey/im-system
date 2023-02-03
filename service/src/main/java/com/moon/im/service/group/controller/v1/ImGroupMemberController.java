package com.moon.im.service.group.controller.v1;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.group.model.req.ImportGroupMemberReq;
import com.moon.im.service.group.model.resp.AddMemberResp;
import com.moon.im.service.group.service.ImGroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@RestController
@RequestMapping("v1/group/member")
public class ImGroupMemberController {

    @Autowired
    ImGroupMemberService groupMemberService;

    @RequestMapping("/importGroupMember")
    public ResponseVO<List<AddMemberResp>> importGroupMember(@RequestBody @Validated ImportGroupMemberReq req) {
        return ResponseVO.successResponse(groupMemberService.importGroupMembers(req));
    }

//    @RequestMapping("/add")
//    public ResponseVO addMember(@RequestBody @Validated AddGroupMemberReq req, Integer appId, String identifier)  {
//        req.setAppId(appId);
//        req.setOperater(identifier);
//        return groupMemberService.addMember(req);
//    }
//
//    @RequestMapping("/remove")
//    public ResponseVO removeMember(@RequestBody @Validated RemoveGroupMemberReq req, Integer appId, String identifier)  {
//        req.setAppId(appId);
//        req.setOperater(identifier);
//        return groupMemberService.removeMember(req);
//    }
//
//    @RequestMapping("/update")
//    public ResponseVO updateGroupMember(@RequestBody @Validated UpdateGroupMemberReq req, Integer appId, String identifier)  {
//        req.setAppId(appId);
//        req.setOperater(identifier);
//        return groupMemberService.updateGroupMember(req);
//    }
//
//    @RequestMapping("/speak")
//    public ResponseVO speak(@RequestBody @Validated SpeaMemberReq req, Integer appId, String identifier)  {
//        req.setAppId(appId);
//        req.setOperater(identifier);
//        return groupMemberService.speak(req);
//    }
}
