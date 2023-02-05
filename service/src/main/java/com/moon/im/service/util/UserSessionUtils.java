package com.moon.im.service.util;

import com.alibaba.fastjson.JSON;
import com.moon.im.common.constant.RedisConstants;
import com.moon.im.common.enums.ImConnectStatusEnum;
import com.moon.im.common.model.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Chanmoey
 * @date 2023年02月05日
 */
@Service
public class UserSessionUtils {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取用户所有的Session
     */
    public List<UserSession> getUserSession(Integer appId, String userId) {

        String userSessionKey = appId + RedisConstants.USER_SESSION + userId;
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(userSessionKey);

        List<UserSession> list = new ArrayList<>();
        Collection<Object> values = entries.values();
        for (Object o : values) {
            String str = (String) o;
            UserSession userSession = JSON.parseObject(str, UserSession.class);
            if (Objects.equals(userSession.getConnectStatus(), ImConnectStatusEnum.ONLINE_STATUS.getCode())) {
                list.add(userSession);
            }

        }
        return list;
    }

    public UserSession getUserSession(Integer appId, String userId, Integer clientType, String imei) {
        String userSessionKey = appId + RedisConstants.USER_SESSION + userId;
        String hashKey = clientType + ":" + imei;
        Object o = stringRedisTemplate.opsForHash().get(userSessionKey, hashKey);
        return JSON.parseObject((String) o, UserSession.class);
    }
}
