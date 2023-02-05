package com.moon.im.codec.pack.group;

import lombok.Data;

/**
 * @author Chanmoey
 * @date 2023年02月05日
 */
@Data
public class UpdateGroupMemberPack {

    private String groupId;

    private String memberId;

    private String alias;

    private String extra;
}
