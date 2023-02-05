package com.moon.im.codec.pack.friendship;

import lombok.Data;


/**
 * @author Chanmoey
 * @date 2023年02月05日
 */
@Data
public class UpdateFriendPack {

    public String fromId;

    private String toId;

    private String remark;

    private Long sequence;
}
