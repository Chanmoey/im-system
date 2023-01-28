package com.moon.im.service.user.model.req;

import com.moon.im.common.model.RequestBase;
import com.moon.im.service.user.dao.ImUserDataEntity;
import lombok.Data;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
@Data
public class ImportUserReq extends RequestBase {

    private List<ImUserDataEntity> userData;

}
