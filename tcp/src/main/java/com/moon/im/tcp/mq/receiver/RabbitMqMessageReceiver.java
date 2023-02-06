package com.moon.im.tcp.mq.receiver;

import com.alibaba.fastjson.JSON;
import com.moon.im.codec.proto.MessagePack;
import com.moon.im.common.constant.RabbitConstants;
import com.moon.im.tcp.mq.RabbitMqFactory;
import com.moon.im.tcp.mq.receiver.process.BaseProcess;
import com.moon.im.tcp.mq.receiver.process.ProcessFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * @author Chanmoey
 * @date 2023年02月02日
 */
@Slf4j
public class RabbitMqMessageReceiver {

    private RabbitMqMessageReceiver() {
    }

    private static String brokerId;

    private static void startReceiverMessage() {
        try {
            Channel channel = RabbitMqFactory.getChannel(RabbitConstants.MESSAGE_SERVICE_2_IM + brokerId);
            channel.queueDeclare(RabbitConstants.MESSAGE_SERVICE_2_IM + brokerId,
                    true, false, false, null);
            channel.queueBind(RabbitConstants.MESSAGE_SERVICE_2_IM + brokerId,
                    RabbitConstants.MESSAGE_SERVICE_2_IM, brokerId);

            channel.basicConsume(RabbitConstants.MESSAGE_SERVICE_2_IM + brokerId, false,
                    new DefaultConsumer(channel) {
                        @Override
                        public void handleDelivery(String consumerTag,
                                                   Envelope envelope,
                                                   AMQP.BasicProperties properties,
                                                   byte[] body) throws IOException {
                            try {
                                String msgSrt = new String(body);
                                log.info(msgSrt);
                                MessagePack messagePack = JSON.parseObject(msgSrt, MessagePack.class);
                                BaseProcess messageProcess = ProcessFactory.getMessageProcess(messagePack.getClientType());
                                messageProcess.process(messagePack);
                                channel.basicAck(envelope.getDeliveryTag(), false);
                            } catch (Exception e) {
                                log.error("process message error, {}", e.getMessage());
                                channel.basicNack(envelope.getDeliveryTag(), false, false);
                            }
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init() {
        startReceiverMessage();
    }

    public static void init(String brokerId) {
        if (StringUtils.isBlank(RabbitMqMessageReceiver.brokerId)) {
            RabbitMqMessageReceiver.brokerId = brokerId;
        }
        startReceiverMessage();
    }
}
