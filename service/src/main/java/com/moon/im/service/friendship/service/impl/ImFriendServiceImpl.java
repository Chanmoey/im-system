package com.moon.im.service.friendship.service.impl;

import com.moon.im.common.ResponseVO;
import com.moon.im.service.friendship.dao.ImFriendShipEntity;
import com.moon.im.service.friendship.dao.mapper.ImFriendShipMapper;
import com.moon.im.service.friendship.model.req.ImportFriendShipReq;
import com.moon.im.service.friendship.model.req.ImportFriendShipResp;
import com.moon.im.service.friendship.service.ImFriendService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年01月29日
 */
@Service
public class ImFriendServiceImpl implements ImFriendService {

    @Autowired
    private ImFriendShipMapper imFriendShipMapper;

    @Override
    public ResponseVO<ImportFriendShipResp> importFriendShip(ImportFriendShipReq req) {
        if (req.getFriendItem().size() > 100) {
            // TODO: 返回超出数量限制
        }

        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();


        req.getFriendItem().forEach(item -> {
            ImFriendShipEntity entity = new ImFriendShipEntity();
            BeanUtils.copyProperties(item, entity);
            entity.setAppId(req.getAppId());
            entity.setFromId(req.getFromId());
            try {
                int insert = imFriendShipMapper.insert(entity);
                if (insert == 1) {
                    successId.add(item.getToId());
                } else {
                    errorId.add(item.getToId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorId.add(item.getToId());
            }
        });

        ImportFriendShipResp resp = new ImportFriendShipResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);

        return ResponseVO.successResponse(resp);
    }
}
