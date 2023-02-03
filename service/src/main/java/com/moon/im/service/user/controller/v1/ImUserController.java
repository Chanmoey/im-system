package com.moon.im.service.user.controller.v1;


import com.moon.im.common.ResponseVO;
import com.moon.im.service.user.model.req.DeleteUserReq;
import com.moon.im.service.user.model.req.GetUserInfoReq;
import com.moon.im.service.user.model.req.GetUserSequenceReq;
import com.moon.im.service.user.model.req.ImportUserReq;
import com.moon.im.service.user.model.resp.DeleteUserResp;
import com.moon.im.service.user.model.resp.GetUserInfoResp;
import com.moon.im.service.user.model.resp.ImportUserResp;
import com.moon.im.service.user.service.ImUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
@RestController
@RequestMapping("/user")
public class ImUserController {
    @Autowired
    private ImUserService imUserService;

    @PostMapping("/importUser")
    public ResponseVO<ImportUserResp> importUser(@RequestBody ImportUserReq req) {
        return ResponseVO.successResponse(imUserService.importUser(req));
    }

    @RequestMapping("/getUserInfo")
    public ResponseVO<GetUserInfoResp> getUserInfo(@RequestBody GetUserInfoReq req) {//@Validated
        return ResponseVO.successResponse(imUserService.getUserInfo(req));
    }

    @PostMapping("/deleteUser")
    public ResponseVO<DeleteUserResp> deleteUser(@RequestBody @Validated DeleteUserReq req) {
        return ResponseVO.successResponse(imUserService.deleteUser(req));
    }
}
