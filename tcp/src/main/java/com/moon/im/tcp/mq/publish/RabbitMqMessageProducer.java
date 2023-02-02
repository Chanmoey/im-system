package com.moon.im.tcp.mq.publish;


import com.alibaba.fastjson.JSON;
import com.moon.im.tcp.mq.RabbitMqFactory;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Chanmoey
 * @date 2023年02月02日
 */
@Slf4j
public class RabbitMqMessageProducer {

    public static void sendMessage(Object message) {
        Channel channel;
        String channelName = "";
        try {
            channel = RabbitMqFactory.getChannel(channelName);
            channel.basicPublish(channelName, "", null,
                    JSON.toJSONString(message).getBytes());
        } catch (Exception e) {
            log.error("send message to rabbitmq error: {}", e.getMessage());
        }
    }
}
