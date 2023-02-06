package com.moon.im.common.constant;

/**
 * @author Chanmoey
 * @date 2023年02月02日
 */
public class RedisConstants {

    /**
     * 用户session: appId + USER_SESSION + 用户id
     */
    public static final String USER_SESSION = ":userSession:";

    /**
     * 用户上线Channel
     */
    public static final String USER_LOGIN_CHANNEL = "signal/channel/LOGIN_USER_INNER_QUEUE";

    /**
     * userSign，格式：appId:userSign:
     */
    public static final String USER_SIGN = "userSign";
}
