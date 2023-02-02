package com.moon.im.tcp.util;

import com.alibaba.fastjson.JSON;
import com.moon.im.common.constant.Constants;
import com.moon.im.common.constant.RedisConstants;
import com.moon.im.common.enums.ImConnectStatusEnum;
import com.moon.im.common.model.UserClientDto;
import com.moon.im.common.model.UserSession;
import com.moon.im.tcp.redis.RedisManager;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Chanmoey
 * @date 2023年02月01日
 */
public class SessionSocketHolder {

    private static final Map<UserClientDto, NioSocketChannel> CHANNELS = new ConcurrentHashMap<>();

    public static void put(Integer appId, String userId, Integer clientType, NioSocketChannel channel) {
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setUserId(userId);
        dto.setClientType(clientType);
        CHANNELS.put(dto, channel);
    }

    public static NioSocketChannel get(Integer appId, String userId, Integer clientType) {
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setUserId(userId);
        dto.setClientType(clientType);
        return CHANNELS.get(dto);
    }

    public static void remove(Integer appId, String userId, Integer clientType) {
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setUserId(userId);
        dto.setClientType(clientType);
        CHANNELS.remove(dto);
    }

    public static void remove(NioSocketChannel channel) {
        CHANNELS.entrySet().stream().filter(entity -> entity.getValue() == channel).forEach(entry -> CHANNELS.remove(entry.getKey()));
    }

    /**
     * 删除session
     */
    public static void removeUserSession(NioSocketChannel channel) {
        // 删除session，删除redis
        UserClientDto dto = removeByChannelAndReturnUserClientDto(channel);

        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<String, String> map = redissonClient.getMap(dto.getAppId() + RedisConstants.USER_SESSION + dto.getUserId());
        map.remove(dto.getClientType().toString());
        channel.close();
    }

    public static void offlineUserSession(NioSocketChannel channel) {
        // 删除session，删除redis
        UserClientDto dto = removeByChannelAndReturnUserClientDto(channel);

        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<String, String> map = redissonClient.getMap(dto.getAppId() + RedisConstants.USER_SESSION + dto.getUserId());
        String sessionStr = map.get(dto.getClientType().toString());
        if (StringUtils.isNotBlank(sessionStr)) {
            UserSession userSession = JSON.parseObject(sessionStr, UserSession.class);
            userSession.setConnectStatus(ImConnectStatusEnum.OFFLINE_STATUS.getCode());
            map.put(dto.getClientType().toString(), JSON.toJSONString(userSession));
        }
        channel.close();
    }

    public static UserClientDto removeByChannelAndReturnUserClientDto(NioSocketChannel channel) {
        Integer appId = (Integer) channel.attr(AttributeKey.valueOf(Constants.APP_ID)).get();
        String userId = (String) channel.attr(AttributeKey.valueOf(Constants.USER_ID)).get();
        Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();
        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setAppId(appId);
        userClientDto.setUserId(userId);
        userClientDto.setClientType(clientType);

        SessionSocketHolder.remove(appId, userId, clientType);
        return userClientDto;
    }
}
