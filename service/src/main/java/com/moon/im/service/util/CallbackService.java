package com.moon.im.service.util;

import com.moon.im.common.ResponseVO;
import com.moon.im.common.config.AppConfig;
import com.moon.im.common.util.HttpRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Chanmoey
 * @date 2023年02月04日
 */
@Component
public class CallbackService {

    private final Logger logger = LoggerFactory.getLogger(CallbackService.class);

    @Autowired
    private HttpRequestUtils httpRequestUtils;

    @Autowired
    private AppConfig appConfig;

    public void callback(Integer appId, String callbackCommand, String jsonBody) {
        try {
            httpRequestUtils.doPost(appConfig.getCallbackUrl(), Object.class, builderUrlParams(appId, callbackCommand),
                    jsonBody, null);
        } catch (Exception e) {
            logger.error("callback 回调 {}: {} 出现异常: {}",
                    callbackCommand, appId, e.getCause());
        }
    }

    public ResponseVO<Object> beforeCallback(Integer appId, String callbackCommand, String jsonBody) {
        try {
            return httpRequestUtils.doPost("", ResponseVO.class, builderUrlParams(appId, callbackCommand),
                    jsonBody, null);
        } catch (Exception e) {
            logger.error("callback 之前回调 {}: {} 出现异常: {}",
                    callbackCommand, appId, e.getCause());
            return ResponseVO.successResponse();
        }
    }

    private Map<String, Object> builderUrlParams(Integer appId, String command) {
        Map<String, Object> map = new HashMap<>();
        map.put("appId", appId);
        map.put("command", command);
        return map;
    }
}
