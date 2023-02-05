package com.moon.im.codec.pack.conversation;

import lombok.Data;

/**
 * @author Chanmoey
 * @date 2023年02月05日
 */
@Data
public class UpdateConversationPack {

    private String conversationId;

    private Integer isMute;

    private Integer isTop;

    private Integer conversationType;

    private Long sequence;

}
