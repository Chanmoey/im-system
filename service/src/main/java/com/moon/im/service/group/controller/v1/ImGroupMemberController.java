package com.moon.im.service.group.controller.v1;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.group.model.req.*;
import com.moon.im.service.group.model.resp.AddMemberResp;
import com.moon.im.service.group.service.ImGroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/group/member")
public class ImGroupMemberController {

    @Autowired
    ImGroupMemberService groupMemberService;

    @RequestMapping("/importGroupMember")
    public ResponseVO<List<AddMemberResp>> importGroupMember(@RequestBody @Validated ImportGroupMemberReq req) {
        return ResponseVO.successResponse(groupMemberService.importGroupMembers(req));
    }

    @RequestMapping("/add")
    public ResponseVO<Object> addMember(@RequestBody @Validated AddGroupMemberReq req) {
        groupMemberService.addMember(req);
        return ResponseVO.successResponse();
    }

    @RequestMapping("/remove")
    public ResponseVO<Object> removeMember(@RequestBody @Validated RemoveGroupMemberReq req) {
        groupMemberService.removeMember(req);
        return ResponseVO.successResponse();
    }

    @RequestMapping("/update")
    public ResponseVO<Object> updateGroupMember(@RequestBody @Validated UpdateGroupMemberReq req) {
        groupMemberService.updateGroupMember(req);
        return ResponseVO.successResponse();
    }

    @RequestMapping("/speak")
    public ResponseVO<Object> speak(@RequestBody @Validated SpeaMemberReq req) {
        groupMemberService.speak(req);
        return ResponseVO.successResponse();
    }
}
