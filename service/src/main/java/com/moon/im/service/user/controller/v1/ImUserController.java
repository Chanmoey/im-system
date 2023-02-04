package com.moon.im.service.user.controller.v1;


import com.moon.im.common.ClientType;
import com.moon.im.common.ResponseVO;
import com.moon.im.common.route.RouteHandle;
import com.moon.im.common.route.RouteInfo;
import com.moon.im.common.util.RouteInfoParseUtil;
import com.moon.im.service.user.model.req.*;
import com.moon.im.service.user.model.resp.DeleteUserResp;
import com.moon.im.service.user.model.resp.GetUserInfoResp;
import com.moon.im.service.user.model.resp.ImportUserResp;
import com.moon.im.service.user.service.ImUserService;
import com.moon.im.service.util.ZKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
@RestController
@RequestMapping("/user")
public class ImUserController {
    @Autowired
    private ImUserService imUserService;

    @Autowired
    private RouteHandle routeHandle;

    @Autowired
    private ZKit zKit;

    @PostMapping("/importUser")
    public ResponseVO<ImportUserResp> importUser(@RequestBody ImportUserReq req) {
        return ResponseVO.successResponse(imUserService.importUser(req));
    }

    @RequestMapping("/getUserInfo")
    public ResponseVO<GetUserInfoResp> getUserInfo(@RequestBody GetUserInfoReq req) {
        return ResponseVO.successResponse(imUserService.getUserInfo(req));
    }

    @PostMapping("/deleteUser")
    public ResponseVO<DeleteUserResp> deleteUser(@RequestBody @Validated DeleteUserReq req) {
        return ResponseVO.successResponse(imUserService.deleteUser(req));
    }

    @PostMapping("/login")
    public ResponseVO<RouteInfo> login(@RequestBody @Validated LoginReq req) {
        imUserService.login(req);

        // 去zk获取一个im的地址，返回给sdk
        List<String> allNode;
        if (req.getClientType() == ClientType.WEB.getCode()) {
            allNode = zKit.getAllWebNode();
        } else {
            allNode = zKit.getAllTcpNode();
        }
        // s = ip:port
        String s = routeHandle.routeServer(allNode, req.getUserId() + req.getAppId());
        RouteInfo routeInfo = RouteInfoParseUtil.parse(s);
        return ResponseVO.successResponse(routeInfo);
    }

    @RequestMapping("/modifyUserInfo")
    public ResponseVO<Object> modifyUserInfo(@RequestBody @Validated ModifyUserInfoReq req){
        imUserService.modifyUserInfo(req);
        return ResponseVO.successResponse();
    }
}
