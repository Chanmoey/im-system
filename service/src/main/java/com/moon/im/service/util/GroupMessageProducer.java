package com.moon.im.service.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.moon.im.common.ClientType;
import com.moon.im.common.enums.command.Command;
import com.moon.im.common.model.ClientInfo;
import com.moon.im.service.group.service.ImGroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年02月06日
 */
@Component
public class GroupMessageProducer {

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    ImGroupMemberService imGroupMemberService;

    public void producer(String userId, Command command, Object data,
                         ClientInfo clientInfo) {
        JSONObject o = (JSONObject) JSON.toJSON(data);
        String groupId = o.getString("groupId");
        List<String> groupMemberId = imGroupMemberService.getGroupMemberId(groupId, clientInfo.getAppId());

        for (String memberId : groupMemberId) {
            // app发起的消息
            if (clientInfo.getClientType() != null
                    && clientInfo.getClientType() != ClientType.WEBAPI.getCode()
                    && memberId.equals(userId)) {
                messageProducer.sendToUserExceptClient(memberId, command,
                        data, clientInfo);
            } else {
                messageProducer.sendToUser(memberId, command, data, clientInfo.getAppId());
            }

        }
    }
}
