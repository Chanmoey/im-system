package com.moon.im.service.user.controller;


import com.moon.im.common.ResponseVO;
import com.moon.im.service.user.model.req.DeleteUserReq;
import com.moon.im.service.user.model.req.GetUserInfoReq;
import com.moon.im.service.user.model.req.GetUserSequenceReq;
import com.moon.im.service.user.model.resp.GetUserInfoResp;
import com.moon.im.service.user.service.ImUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
@RestController
@RequestMapping("/user")
public class ImUserController {
    @Autowired
    private ImUserService imUserService;

    @RequestMapping("/getUserInfo")
    public ResponseVO<GetUserInfoResp> getUserInfo(@RequestBody GetUserInfoReq req, Integer appId) {//@Validated
        req.setAppId(appId);
        return imUserService.getUserInfo(req);
    }

    @PostMapping("/deleteUser")
    public ResponseVO<Object> deleteUser(@RequestBody @Validated DeleteUserReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.deleteUser(req);
    }

    @RequestMapping("/getUserSequence")
    public ResponseVO<Object> getUserSequence(@RequestBody @Validated
                                              GetUserSequenceReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.getUserSequence(req);
    }
}
