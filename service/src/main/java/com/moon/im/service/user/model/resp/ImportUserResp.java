package com.moon.im.service.user.model.resp;

import lombok.Data;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
@Data
public class ImportUserResp {

    private List<String> successId;
    private List<String> errorId;
}
