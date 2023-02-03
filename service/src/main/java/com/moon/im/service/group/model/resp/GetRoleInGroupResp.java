package com.moon.im.service.group.model.resp;

import lombok.Data;

/**
 * @author Chanmoey
 * @date 2023年02月03日
 */
@Data
public class GetRoleInGroupResp {

    private Long groupMemberId;

    private String memberId;

    private Integer role;

    private Long speakDate;

}
