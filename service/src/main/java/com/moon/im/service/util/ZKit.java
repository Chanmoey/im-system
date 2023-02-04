package com.moon.im.service.util;

import com.moon.im.common.constant.Constants;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ZKit {

    @Autowired
    private ZkClient zkClient;

    /**
     * get all TCP server node from zookeeper
     */
    public List<String> getAllTcpNode() {
        return zkClient.getChildren(Constants.IM_CORE_ZK_ROOT + Constants.IM_CORE_ZK_ROOT_TCP);
    }

    /**
     * get all WEB server node from zookeeper
     */
    public List<String> getAllWebNode() {
        return zkClient.getChildren(Constants.IM_CORE_ZK_ROOT + Constants.IM_CORE_ZK_ROOT_WEB);
    }
}
