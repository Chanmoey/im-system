package com.moon.im.service.friendship.model.req;

import com.moon.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Chanmoey
 * @date 2023年01月30日
 */
@Data
public class DeleteFriendReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;
    @NotBlank(message = "toId不能为空")
    private String toId;
}
