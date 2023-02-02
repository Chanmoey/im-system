package com.moon.im.tcp.handler;

import com.moon.im.common.constant.Constants;
import com.moon.im.tcp.util.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理心跳检测
 *
 * @author Chanmoey
 * @date 2023年02月02日
 */
@Slf4j
public class HearBeatHandler extends ChannelInboundHandlerAdapter {

    private Long hearBeatTime;

    public HearBeatHandler(Long hearBeatTime) {
        this.hearBeatTime = hearBeatTime;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.info("read idle");
            }
            if (event.state() == IdleState.WRITER_IDLE) {
                log.info("write idle");
            }
            if (event.state() == IdleState.ALL_IDLE) {
                log.info("all idle");
                Long lastReadTime = (Long) ctx.channel().attr(AttributeKey.valueOf(Constants.READ_TIME)).get();
                long now = System.currentTimeMillis();

                if (lastReadTime != null && now - lastReadTime > hearBeatTime) {
                    // 离线（不是退出登录）
                    SessionSocketHolder.offlineUserSession((NioSocketChannel) ctx.channel());
                }
            }
        }
    }
}
