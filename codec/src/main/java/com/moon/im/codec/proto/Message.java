package com.moon.im.codec.proto;

import lombok.Data;

/**
 * @author Chanmoey
 * @date 2023年02月01日
 */
@Data
public class Message {

    private MessageHeader messageHeader;

    private Object messagePack;

    @Override
    public String toString() {
        return "Message{" +
                "messageHeader=" + messageHeader +
                ", messagePack=" + messagePack +
                '}';
    }


}
