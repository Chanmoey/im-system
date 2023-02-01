package com.moon.im.tcp.redis;

import com.moon.im.codec.config.BootstrapConfig;
import org.redisson.api.RedissonClient;

/**
 * @author Chanmoey
 * @date 2023年02月01日
 */
public class RedisManager {

    private static RedissonClient redissonClient;

    public static void init(BootstrapConfig config) {
        SingleClientStrategy singleClientStrategy = new SingleClientStrategy();
        redissonClient = singleClientStrategy.getRedissonClient(config.getLim().getRedis());
    }

    public static RedissonClient getRedissonClient() {
        return redissonClient;
    }
}
