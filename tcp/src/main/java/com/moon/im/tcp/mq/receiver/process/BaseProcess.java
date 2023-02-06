package com.moon.im.tcp.mq.receiver.process;

import com.moon.im.codec.proto.MessagePack;
import com.moon.im.tcp.util.SessionSocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author Chanmoey
 * @date 2023年02月06日
 */
public abstract class BaseProcess {

    public abstract void processBefore();

    public void process(MessagePack messagePack) {
        processBefore();
        NioSocketChannel channel = SessionSocketHolder.get(
                messagePack.getAppId(), messagePack.getToId(),
                messagePack.getClientType(), messagePack.getImei()
        );
        if (channel != null) {
            channel.writeAndFlush(messagePack);
        }
        processAfter();
    }

    public abstract void processAfter();
}
