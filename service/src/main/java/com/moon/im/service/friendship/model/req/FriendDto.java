package com.moon.im.service.friendship.model.req;

import lombok.Data;

/**
 * @author Chanmoey
 * @date 2023年01月29日
 */
@Data
public class FriendDto {

    private String toId;

    private String remark;

    private String addSource;

    private String extra;
}
