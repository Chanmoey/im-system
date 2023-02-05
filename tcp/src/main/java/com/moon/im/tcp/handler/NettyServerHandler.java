package com.moon.im.tcp.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.moon.im.codec.pack.LoginPack;
import com.moon.im.codec.proto.Message;
import com.moon.im.common.constant.Constants;
import com.moon.im.common.constant.RedisConstants;
import com.moon.im.common.enums.ImConnectStatusEnum;
import com.moon.im.common.enums.command.SystemCommand;
import com.moon.im.common.model.UserClientDto;
import com.moon.im.common.model.UserSession;
import com.moon.im.tcp.redis.RedisManager;
import com.moon.im.tcp.util.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * @author Chanmoey
 * @date 2023年02月01日
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    public static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

    private final Integer brokerId;

    public NettyServerHandler(Integer brokerId) {
        this.brokerId = brokerId;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

        int command = message.getMessageHeader().getCommand();
        if (command == SystemCommand.LOGIN.getCommand()) {
            // 解析用户ID
            LoginPack loginPack = JSON.parseObject(JSON.toJSONString(message.getMessagePack()), new TypeReference<LoginPack>() {
            }.getType());

            ctx.channel().attr(AttributeKey.valueOf(Constants.APP_ID)).set(message.getMessageHeader().getAppId());
            ctx.channel().attr(AttributeKey.valueOf(Constants.USER_ID)).set(loginPack.getUserId());
            ctx.channel().attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).set(message.getMessageHeader().getClientType());
            ctx.channel().attr(AttributeKey.valueOf(Constants.IMEI)).set(message.getMessageHeader().getImei());

            UserSession userSession = new UserSession();
            userSession.setAppId(message.getMessageHeader().getAppId());
            userSession.setClientType(message.getMessageHeader().getClientType());
            userSession.setUserId(loginPack.getUserId());
            userSession.setConnectStatus(ImConnectStatusEnum.ONLINE_STATUS.getCode());
            userSession.setBrokerId(brokerId);
            userSession.setImei(message.getMessageHeader().getImei());
            try {
                InetAddress localHost = InetAddress.getLocalHost();
                userSession.setBrokerHost(localHost.getHostAddress());
            } catch (Exception e) {
                log.error("get localhost error, {}", e.getMessage());
            }

            // 将登录Session保存到Redis
            RedissonClient redissonClient = RedisManager.getRedissonClient();
            RMap<String, String> map = redissonClient.getMap(message.getMessageHeader().getAppId() + RedisConstants.USER_SESSION + loginPack.getUserId());
            map.put(message.getMessageHeader().getClientType() + ":" +
                    message.getMessageHeader().getImei(), JSON.toJSONString(userSession));
            // 将channel进行保存
            SessionSocketHolder.put(message.getMessageHeader().getAppId(), loginPack.getUserId(),
                    message.getMessageHeader().getClientType(), message.getMessageHeader().getImei(),
                    (NioSocketChannel) ctx.channel());

            // 广播用户登录消息，其他服务器接受后，进行多端登录处理
            UserClientDto dto = new UserClientDto();
            dto.setImei(message.getMessageHeader().getImei());
            dto.setUserId(loginPack.getUserId());
            dto.setClientType(message.getMessageHeader().getClientType());
            dto.setAppId(message.getMessageHeader().getAppId());
            RTopic topic = redissonClient.getTopic(RedisConstants.USER_LOGIN_CHANNEL);
            topic.publish(JSON.toJSONString(dto));
        } else if (command == SystemCommand.LOGOUT.getCommand()) {
            // 删除session，删除redis
            SessionSocketHolder.removeUserSession((NioSocketChannel) ctx.channel());
        } else if (command == SystemCommand.PING.getCommand()) {
            ctx.channel().attr(AttributeKey.valueOf(Constants.READ_TIME)).set(System.currentTimeMillis());
        }
    }
}
