package com.moon.im.tcp.register;

import com.moon.im.common.constant.Constants;
import org.I0Itec.zkclient.ZkClient;

/**
 * ZK节点设计：/im-coreRoot/模块/ip:port
 *
 * @author Chanmoey
 * @date 2023年02月02日
 */
public class ZKit {

    private ZkClient zkClient;

    public ZKit(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    /**
     * /im-coreRoot/模块
     */
    public void createRootNode() {
        boolean exists = zkClient.exists(Constants.IM_CORE_ZK_ROOT);
        if (!exists) {
            zkClient.createPersistent(Constants.IM_CORE_ZK_ROOT);
        }

        boolean tcpExists = zkClient.exists(Constants.IM_CORE_ZK_ROOT + Constants.IM_CORE_ZK_ROOT_TCP);
        if (!tcpExists) {
            zkClient.createPersistent(Constants.IM_CORE_ZK_ROOT + Constants.IM_CORE_ZK_ROOT_TCP);
        }

        boolean webExists = zkClient.exists(Constants.IM_CORE_ZK_ROOT + Constants.IM_CORE_ZK_ROOT_WEB);
        if (!webExists) {
            zkClient.createPersistent(Constants.IM_CORE_ZK_ROOT + Constants.IM_CORE_ZK_ROOT_WEB);
        }
    }

    /**
     * ip:port
     * @param path ip:port
     */
    public void createNode(String path) {
        if (!zkClient.exists(path)) {
            zkClient.createPersistent(path);
        }
    }
}
