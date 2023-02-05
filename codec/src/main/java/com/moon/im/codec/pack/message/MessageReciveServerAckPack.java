package com.moon.im.codec.pack.message;

import lombok.Data;

/**
 * @author Chanmoey
 * @date 2023年02月05日
 */
@Data
public class MessageReciveServerAckPack {

    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageSequence;

    private Boolean serverSend;
}
