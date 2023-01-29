package com.moon.im.service.friendship.model.resp;

import lombok.Data;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年01月29日
 */
@Data
public class ImportFriendShipResp {
    private List<String> successId;
    private List<String> errorId;
}
