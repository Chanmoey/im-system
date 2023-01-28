package com.moon.im.service.user.model.resp;

import com.moon.im.common.model.RequestBase;
import com.moon.im.service.user.dao.ImUserDataEntity;
import lombok.Data;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
@Data
public class ImportUserResp extends RequestBase {

    private List<String> successId;
    private List<String> errorId;
}
