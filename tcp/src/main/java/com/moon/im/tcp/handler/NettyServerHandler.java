package com.moon.im.tcp.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.moon.im.codec.pack.LoginPack;
import com.moon.im.codec.proto.Message;
import com.moon.im.common.constant.RedisConstants;
import com.moon.im.common.enums.ImConnectStatusEnum;
import com.moon.im.common.enums.command.SystemCommand;
import com.moon.im.common.model.UserSession;
import com.moon.im.tcp.redis.RedisManager;
import com.moon.im.tcp.util.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

/**
 * @author Chanmoey
 * @date 2023年02月01日
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

        int command = message.getMessageHeader().getCommand();
        if (command == SystemCommand.LOGIN.getCommand()) {
            // 解析用户ID
            LoginPack loginPack = JSON.parseObject(JSONObject.toJSONString(message.getMessagePack()),
                    new TypeReference<LoginPack>() {
                    }.getType());

            ctx.channel().attr(AttributeKey.valueOf("userId")).set(loginPack.getUserId());

            UserSession userSession = new UserSession();
            userSession.setAppId(message.getMessageHeader().getAppId());
            userSession.setClientType(message.getMessageHeader().getClientType());
            userSession.setUserId(loginPack.getUserId());
            userSession.setConnectStatus(ImConnectStatusEnum.ONLINE_STATUS.getCode());

            // 将用户状态存储到Redis，以Map的形式 key : {clientType : channel}
            RedissonClient redissonClient = RedisManager.getRedissonClient();
            RMap<String, String> map = redissonClient.getMap(
                    message.getMessageHeader().getAppId()
                            + RedisConstants.USER_SESSION
                            + loginPack.getUserId());
            map.put(String.valueOf(message.getMessageHeader().getClientType()),
                    JSON.toJSONString(userSession));

            // 将channel进行保存
            SessionSocketHolder.put(loginPack.getUserId(), (NioSocketChannel) ctx.channel());
        }
    }
}
