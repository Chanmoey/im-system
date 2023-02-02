package com.moon.im.tcp.mq;

import com.moon.im.codec.config.BootstrapConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * @author Chanmoey
 * @date 2023年02月02日
 */
public class RabbitMqFactory {

    private static ConnectionFactory factory;

    private static Channel defaultChannel;

    private static final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    public static void init(BootstrapConfig.Rabbitmq cfg) {
        if (factory == null) {
            factory = new ConnectionFactory();
            factory.setHost(cfg.getHost());
            factory.setPort(cfg.getPort());
            factory.setUsername(cfg.getUserName());
            factory.setPassword(cfg.getPassword());
            factory.setVirtualHost(cfg.getVirtualHost());
        }
    }

    private static Connection getConnection() throws IOException, TimeoutException {
        return factory.newConnection();
    }

    public static Channel getChannel(String channelName) throws IOException, TimeoutException {

        Channel channel = channelMap.get(channelName);

        if (channel == null) {
            channel = getConnection().createChannel();
            channelMap.put(channelName, channel);
        }

        return channel;
    }
}
