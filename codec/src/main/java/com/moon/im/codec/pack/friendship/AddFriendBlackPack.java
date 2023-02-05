package com.moon.im.codec.pack.friendship;

import lombok.Data;

/**
 * @author Chanmoey
 * @date 2023年02月05日
 */
@Data
public class AddFriendBlackPack {
    private String fromId;

    private String toId;

    private Long sequence;
}
