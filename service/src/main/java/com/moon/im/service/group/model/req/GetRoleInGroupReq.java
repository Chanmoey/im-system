package com.moon.im.service.group.model.req;

import com.moon.im.common.model.RequestBase;
import lombok.Data;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年02月03日
 */
@Data
public class GetRoleInGroupReq extends RequestBase {

    private String groupId;

    private List<String> memberId;
}
