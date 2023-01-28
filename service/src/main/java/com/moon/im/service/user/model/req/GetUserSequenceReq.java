package com.moon.im.service.user.model.req;

import com.moon.im.common.model.RequestBase;
import lombok.Data;


@Data
public class GetUserSequenceReq extends RequestBase {

    private String userId;

}
