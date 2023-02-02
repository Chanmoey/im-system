package com.moon.im.tcp.mq.receiver;

import com.alibaba.fastjson.JSON;
import com.moon.im.codec.proto.MessagePack;
import com.moon.im.common.ClientType;
import com.moon.im.common.constant.Constants;
import com.moon.im.common.constant.RedisConstants;
import com.moon.im.common.enums.DeviceMultiLoginEnum;
import com.moon.im.common.enums.command.SystemCommand;
import com.moon.im.common.model.UserClientDto;
import com.moon.im.tcp.redis.RedisManager;
import com.moon.im.tcp.util.SessionSocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.redisson.api.RTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 接收来自Redis的登录消息：
 * 多端同步：
 * 1. 单端登录：只支持一个客户端在线，剔除除了本消息的clientType + imei的设备
 * 2. 双端登录：允许pc/mobile其中一端+web端，只留下mobile和pc其中一个和web
 * 3. 三端登录：允许mobile+pc+web同时登录一台设备，踢掉同端的其他设备
 * 4. 多端登录，不做任何处理
 *
 * @author Chanmoey
 * @date 2023年02月03日
 */
public class UserLoginMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(UserLoginMessageListener.class);

    private final Integer loginModel;

    public UserLoginMessageListener(Integer loginModel) {
        this.loginModel = loginModel;
    }

    public void listenerUserLogin() {
        RTopic topic = RedisManager.getRedissonClient().getTopic(RedisConstants.USER_LOGIN_CHANNEL);
        topic.addListener(String.class, (charSequence, msg) -> {
            logger.info("user login success: {}", msg);

            UserClientDto dto = JSON.parseObject(msg, UserClientDto.class);
            // 当前登录的是web，则不做任何处理
            if (dto.getClientType() == ClientType.WEB.getCode()) {
                return;
            }

            // 当前登录的唯一标识
            String clientImei = dto.getClientType() + ':' + dto.getImei();
            // 拿出本台tcp服务器中，用户建立的channel
            List<NioSocketChannel> userAllChannel = SessionSocketHolder
                    .getUserAllChannel(dto.getAppId(), dto.getUserId());

            for (NioSocketChannel channel : userAllChannel) {
                if (loginModel == DeviceMultiLoginEnum.ONE.getLoginMode()) {
                    Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();
                    String imei = (String) channel.attr(AttributeKey.valueOf(Constants.IMEI)).get();
                    if (!(clientType + ':' + imei).equals(clientImei)) {
                        // 踢掉这个客户端，保留新的
                        // 服务端不能主动断开与客户端的连接，所以要先告知客户端其它端登录，是否要确认退出
                        MessagePack<Object> pack = new MessagePack<>();
                        pack.setToId((String) channel.attr(AttributeKey.valueOf(Constants.USER_ID)).get());
                        pack.setUserId((String) channel.attr(AttributeKey.valueOf(Constants.USER_ID)).get());
                        pack.setCommand(SystemCommand.MULTI_LOGIN.getCommand());
                        channel.writeAndFlush(pack);
                    }
                } else if (loginModel == DeviceMultiLoginEnum.TWO.getLoginMode()) {

                    Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();
                    if (clientType == ClientType.WEB.getCode()) {
                        continue;
                    }

                    String imei = (String) channel.attr(AttributeKey.valueOf(Constants.IMEI)).get();
                    if (!(clientType + ':' + imei).equals(clientImei)) {
                        // 踢掉这个客户端，保留新的
                        MessagePack<Object> pack = new MessagePack<>();
                        pack.setToId((String) channel.attr(AttributeKey.valueOf(Constants.USER_ID)).get());
                        pack.setUserId((String) channel.attr(AttributeKey.valueOf(Constants.USER_ID)).get());
                        pack.setCommand(SystemCommand.MULTI_LOGIN.getCommand());
                        channel.writeAndFlush(pack);
                    }

                } else if (loginModel == DeviceMultiLoginEnum.THREE.getLoginMode()) {
                    Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();
                    String imei = (String) channel.attr(AttributeKey.valueOf(Constants.IMEI)).get();

                    // 判断是否同端
                    boolean isSameClient = bothMobileClient(clientType, dto.getClientType());

                    // 不是同手机端，判断是不是同电脑端
                    if (!isSameClient) {
                        isSameClient = bothPcClient(clientType, dto.getClientType());
                    }

                    if (isSameClient && !(clientType + ':' + imei).equals(clientImei)) {
                        // 踢掉这个客户端，保留新的
                        MessagePack<Object> pack = new MessagePack<>();
                        pack.setToId((String) channel.attr(AttributeKey.valueOf(Constants.USER_ID)).get());
                        pack.setUserId((String) channel.attr(AttributeKey.valueOf(Constants.USER_ID)).get());
                        pack.setCommand(SystemCommand.MULTI_LOGIN.getCommand());
                        channel.writeAndFlush(pack);
                    }
                }
            }
        });
    }

    private boolean bothMobileClient(Integer type1, Integer type2) {
        return (type1 == ClientType.IOS.getCode() ||
                type1 == ClientType.ANDROID.getCode()) &&
                (type2 == ClientType.IOS.getCode() ||
                        type2 == ClientType.ANDROID.getCode());
    }

    private boolean bothPcClient(Integer type1, Integer type2) {
        return (type1 == ClientType.MAC.getCode() ||
                type1 == ClientType.WINDOWS.getCode()) &&
                (type2 == ClientType.MAC.getCode() ||
                        type2 == ClientType.WINDOWS.getCode());
    }
}
