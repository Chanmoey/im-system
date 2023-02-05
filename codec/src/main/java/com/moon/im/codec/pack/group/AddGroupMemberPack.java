package com.moon.im.codec.pack.group;

import lombok.Data;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年02月05日
 */
@Data
public class AddGroupMemberPack {

    private String groupId;

    private List<String> members;

}
