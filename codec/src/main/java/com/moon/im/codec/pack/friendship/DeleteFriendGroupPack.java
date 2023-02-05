package com.moon.im.codec.pack.friendship;

import lombok.Data;

/**
 * @author Chanmoey
 * @date 2023年02月05日
 */
@Data
public class DeleteFriendGroupPack {
    public String fromId;

    private String groupName;

    /** 序列号*/
    private Long sequence;
}
