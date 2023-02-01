package com.moon.im.tcp.util;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Chanmoey
 * @date 2023年02月01日
 */
public class SessionSocketHolder {

    private static final Map<String, NioSocketChannel> CHANNELS = new ConcurrentHashMap<>();

    public static void put(String userId, NioSocketChannel channel) {
        CHANNELS.put(channel.remoteAddress().getHostName(), channel);
    }

    public static NioSocketChannel get(String userId) {
        return CHANNELS.get(userId);
    }
}
