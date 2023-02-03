package com.moon.im.service.group.model.req;

import com.moon.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Chanmoey
 * @date 2023年02月03日
 */
@Data
public class DestroyGroupReq extends RequestBase {

    @NotNull(message = "群id不能为空")
    private String groupId;

}
