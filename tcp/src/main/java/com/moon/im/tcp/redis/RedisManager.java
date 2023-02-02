package com.moon.im.tcp.redis;

import com.moon.im.codec.config.BootstrapConfig;
import com.moon.im.tcp.mq.receiver.UserLoginMessageListener;
import org.redisson.api.RedissonClient;

/**
 * @author Chanmoey
 * @date 2023年02月01日
 */
public class RedisManager {

    private RedisManager() {
    }

    private static RedissonClient redissonClient;

    private static Integer loginModel;

    public static void init(BootstrapConfig config) {
        loginModel = config.getLim().getLoginModel();
        SingleClientStrategy singleClientStrategy = new SingleClientStrategy();
        redissonClient = singleClientStrategy.getRedissonClient(config.getLim().getRedis());
        UserLoginMessageListener userLoginMessageListener =
                new UserLoginMessageListener(loginModel);
        userLoginMessageListener.listenerUserLogin();
    }

    public static RedissonClient getRedissonClient() {
        return redissonClient;
    }
}
