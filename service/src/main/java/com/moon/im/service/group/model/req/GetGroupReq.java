package com.moon.im.service.group.model.req;

import com.moon.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Chanmoey
 * @date 2023年02月03日
 */
@Data
public class GetGroupReq extends RequestBase {

    @NotBlank(message = "groupId不能为空")
    private String groupId;

}
