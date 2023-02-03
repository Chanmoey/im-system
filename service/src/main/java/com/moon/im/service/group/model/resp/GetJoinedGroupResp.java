package com.moon.im.service.group.model.resp;

import com.moon.im.service.group.dao.ImGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年02月03日
 */
@Data
public class GetJoinedGroupResp {

    private Integer totalCount;

    private List<ImGroupEntity> groupList;

}
