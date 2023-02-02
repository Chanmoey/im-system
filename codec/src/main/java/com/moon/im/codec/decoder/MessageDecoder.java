package com.moon.im.codec.decoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.moon.im.codec.proto.Message;
import com.moon.im.codec.proto.MessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 消息解码类
 * header：
 * 指令 4
 * 版本号 4
 * clientType 4
 * 消息类型 4
 * appId 4
 * imei长度 4
 * bodyLen 4
 * -----------------
 * body：
 * imei号
 * 请求体
 *
 * @author Chanmoey
 * @date 2023年02月01日
 */
public class MessageDecoder extends ByteToMessageDecoder {

    private static final Integer HEADER_LENGTH = 28;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {

        if (buf.readableBytes() < HEADER_LENGTH) {
            return;
        }

        int command = buf.readInt();
        int version = buf.readInt();
        int clientType = buf.readInt();
        int messageType = buf.readInt();
        int appId = buf.readInt();
        int imeiLength = buf.readInt();
        int bodyLength = buf.readInt();

        if (buf.readableBytes() < imeiLength + bodyLength) {
            // 如果数据不够，则重置读取位置，等数据传输完成再读
            buf.resetReaderIndex();
            return;
        }

        // 读取硬件表示imei
        byte[] imeiData = new byte[imeiLength];
        buf.readBytes(imeiData);
        String imei = new String(imeiData);

        // 读取硬件表示imei
        byte[] bodyData = new byte[bodyLength];
        buf.readBytes(bodyData);

        MessageHeader header = MessageHeader.create(command, version, clientType,
                appId, messageType, imeiLength, bodyLength, imei);


        Message message = new Message();
        message.setMessageHeader(header);
        if (messageType == 0x0) {
            String body = new String(bodyData);
            JSONObject parse = (JSONObject) JSON.parse(body);
            message.setMessagePack(parse);
        }
        out.add(message);

        // 设置阅读位置
        buf.markReaderIndex();
    }
}
