package com.moon.im.codec.pack.friendship;

import lombok.Data;

/**
 * @author Chanmoey
 * @date 2023年02月05日
 */
@Data
public class ReadAllFriendRequestPack {

    private String fromId;

    private Long sequence;
}
