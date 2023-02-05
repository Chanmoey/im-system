package com.moon.im.codec.pack.friendship;

import lombok.Data;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年02月05日
 */
@Data
public class AddFriendGroupMemberPack {

    public String fromId;

    private String groupName;

    private List<String> toIds;

    /** 序列号*/
    private Long sequence;
}
