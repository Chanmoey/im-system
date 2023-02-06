package com.moon.im.service.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.moon.im.common.BaseErrorCode;
import com.moon.im.common.config.AppConfig;
import com.moon.im.common.constant.RedisConstants;
import com.moon.im.common.enums.GateWayErrorCode;
import com.moon.im.common.exception.ApplicationExceptionEnum;
import com.moon.im.common.util.SigAPI;
import com.moon.im.service.user.service.ImUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Chanmoey
 * @date 2023年02月07日
 */
@Slf4j
@Component
public class IdentityCheck {

    @Autowired
    private ImUserService imUserService;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public ApplicationExceptionEnum checkUserSig(String identifier, Long appId, String userSig) {

        long now = System.currentTimeMillis() / 1000;
        String redisKey = appId + ":" + RedisConstants.USER_SIGN + ":" + identifier + userSig;
        String cacheUserSig = stringRedisTemplate.opsForValue().get(redisKey);
        if (!StringUtils.isBlank(cacheUserSig) && Long.parseLong(cacheUserSig) > now) {
            return BaseErrorCode.SUCCESS;
        }

        // 解密
        JSONObject jsonObject = SigAPI.decodeUserSig(userSig);

        // 匹配
        long expireTime = 0L;
        long expireSec = 0L;
        String decoerAppId = "";
        String decoderidentifier = "";

        try {
            decoerAppId = jsonObject.getString("TLS.appId");
            decoderidentifier = jsonObject.getString("TLS.identifier");
            String expireStr = jsonObject.get("TLS.expire").toString();
            String expireTimeStr = jsonObject.get("TLS.expireTime").toString();
            expireSec = Long.parseLong(expireStr);
            expireTime = Long.parseLong(expireTimeStr) + expireSec;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("checkUserSig-error:{}", e.getMessage());
        }

        if (!decoderidentifier.equals(identifier)) {
            return GateWayErrorCode.USER_SIGN_OPERATE_NOT_MATE;
        }

        if (!decoerAppId.equals(String.valueOf(appId))) {
            return GateWayErrorCode.USER_SIGN_IS_ERROR;
        }

        if (expireSec == 0L) {
            return GateWayErrorCode.USER_SIGN_IS_EXPIRED;
        }

        if (expireTime < now) {
            return GateWayErrorCode.USER_SIGN_IS_EXPIRED;
        }

        long eTime = expireTime - now;
        stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(expireTime), eTime, TimeUnit.SECONDS);

        return BaseErrorCode.SUCCESS;
    }
}
