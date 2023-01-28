package com.moon.im.service.user.service.impl;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.user.service.ImUserService;
import com.moon.im.service.user.dao.mapper.ImUserDataMapper;
import com.moon.im.service.user.model.req.ImportUserReq;
import com.moon.im.service.user.model.resp.ImportUserResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年01月28日
 */
@Service
public class ImUserServiceImpl implements ImUserService {

    @Autowired
    private ImUserDataMapper imUserDataMapper;

    @Override
    public ResponseVO<ImportUserResp> importUser(ImportUserReq req) {

        if (req.getUserList().size() > 100) {
            // TODO 返回数量过多
        }

        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();
        req.getUserList().forEach(e -> {
            try {

                e.setAppId(req.getAppId());

                int insert = imUserDataMapper.insert(e);
                if (insert == 1) {
                    successId.add(e.getUserId());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorId.add(e.getUserId());
            }
        });
        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);
        return ResponseVO.successResponse(resp);
    }
}
