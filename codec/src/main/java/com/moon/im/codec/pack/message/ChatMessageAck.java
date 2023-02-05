package com.moon.im.codec.pack.message;

import lombok.Data;

/**
 * @author Chanmoey
 * @date 2023年02月05日
 */
@Data
public class ChatMessageAck {

    private String messageId;
    private Long messageSequence;

    public ChatMessageAck(String messageId) {
        this.messageId = messageId;
    }

    public ChatMessageAck(String messageId,Long messageSequence) {
        this.messageId = messageId;
        this.messageSequence = messageSequence;
    }

}
