package com.moon.im.service.friendship.model.req;

import com.moon.im.common.enums.FriendShipStatusEnum;
import com.moon.im.common.model.RequestBase;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年01月29日
 */
@Data
public class ImportFriendShipReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    private List<ImportFriendDto> friendItem;

    @Getter
    public static class ImportFriendDto {
        private String toId;

        private String remark;

        private String addSource;

        private Integer status = FriendShipStatusEnum.FRIEND_STATUS_NO_FRIEND.getCode();

        private Integer black = FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode();
    }
}
