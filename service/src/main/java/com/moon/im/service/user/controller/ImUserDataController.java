package com.moon.im.service.user.controller;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.user.model.req.ImportUserReq;
import com.moon.im.service.user.model.resp.ImportUserResp;
import com.moon.im.service.user.service.ImUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
@RestController
@RequestMapping("/user")
public class ImUserDataController {

    @Autowired
    private ImUserService imUserService;

    @GetMapping("/importUser")
    public ResponseVO<ImportUserResp> importUser(@RequestBody ImportUserReq req,
                                                 @RequestParam Integer appId) {
        req.setAppId(appId);

        return imUserService.importUser(req);
    }
}
