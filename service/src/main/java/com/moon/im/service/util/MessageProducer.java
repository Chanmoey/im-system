package com.moon.im.service.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.moon.im.codec.proto.MessagePack;
import com.moon.im.common.enums.command.Command;
import com.moon.im.common.model.ClientInfo;
import com.moon.im.common.model.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author Chanmoey
 * @date 2023年02月05日
 */
@Component
@Slf4j
public class MessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserSessionUtils userSessionUtils;

    public boolean sendMessage(UserSession session, Object msg) {
        try {
            log.info("send message == {}", msg);
            rabbitTemplate.convertAndSend("", session.getBrokerId() + "", msg);
            return true;
        } catch (Exception e) {
            log.error("send message error, {}", e.getMessage());
            return false;
        }
    }

    /**
     * 包装数据，然后发送
     */
    public boolean sendPack(String toId, Command command, Object msg, UserSession userSession) {
        MessagePack<JSONObject> messagePack = new MessagePack<>();
        messagePack.setCommand(command.getCommand());
        messagePack.setToId(toId);
        messagePack.setClientType(userSession.getClientType());
        messagePack.setAppId(userSession.getAppId());
        messagePack.setImei(userSession.getImei());
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(msg));
        messagePack.setData(jsonObject);

        String packStr = JSON.toJSONString(messagePack);
        return sendMessage(userSession, packStr);
    }

    /**
     * 发送给用户的所有客户端
     */
    public void sendToUser(String toId, Command command, Object data, Integer appId) {
        List<UserSession> userSessions = userSessionUtils.getUserSession(appId, toId);

        for (UserSession userSession : userSessions) {
            sendPack(toId, command, data, userSession);
        }
    }

    /**
     * 如果是管理员操作，则需要发送给所有人，如果是用户发送，则需要发送给除了自己之外的所有人
     */
    public void sendToUser(String toId, Integer clientType,String imei, Command command,
                           Object data, Integer appId){
        if(clientType != null && StringUtils.isNotBlank(imei)){
            ClientInfo clientInfo = new ClientInfo(appId, clientType, imei);
            sendToUserExceptClient(toId,command,data,clientInfo);
        }else{
            sendToUser(toId,command,data,appId);
        }
    }

    /**
     * 发送给用户的指定客户端
     *
     * @param clientInfo 对方的客户端信息
     */
    public void sendToUser(String toId, Command command, Object data, ClientInfo clientInfo) {
        UserSession userSession = userSessionUtils
                .getUserSession(clientInfo.getAppId(), toId,
                        clientInfo.getClientType(), clientInfo.getImei());

        sendPack(toId, command, data, userSession);
    }

    /**
     * 发送给用户除了某一端的其他端
     *
     * @param clientInfo 需要排除的客户端信息
     */
    public void sendToUserExceptClient(String toId, Command command
            , Object data, ClientInfo clientInfo) {
        List<UserSession> allSession = userSessionUtils
                .getUserSession(clientInfo.getAppId(), toId);
        for (UserSession session : allSession) {
            if (!isMatch(session, clientInfo)) {
                sendPack(toId, command, data, session);
            }
        }
    }

    private boolean isMatch(UserSession sessionDto, ClientInfo clientInfo) {
        return Objects.equals(sessionDto.getAppId(), clientInfo.getAppId())
                && Objects.equals(sessionDto.getImei(), clientInfo.getImei())
                && Objects.equals(sessionDto.getClientType(), clientInfo.getClientType());
    }
}
