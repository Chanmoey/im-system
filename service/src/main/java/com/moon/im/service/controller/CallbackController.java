package com.moon.im.service.controller;

import com.alibaba.fastjson.JSON;
import com.moon.im.common.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chanmoey
 * @date 2023年02月05日
 */
@RestController
public class CallbackController {

    private static final Logger logger = LoggerFactory.getLogger(CallbackController.class);

    @RequestMapping("/callback")
    public ResponseVO<Object> callback(@RequestBody Object req, String command, Integer appId) {
        logger.info("{} 收到{}回调数据：{}", appId, command, JSON.toJSONString(req));
        return ResponseVO.successResponse();
    }
}
