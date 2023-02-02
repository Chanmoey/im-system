package com.moon.im.tcp.register;

import com.moon.im.codec.config.BootstrapConfig;
import com.moon.im.common.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Chanmoey
 * @date 2023年02月02日
 */
public class RegistryZK implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryZK.class);

    private static final char PATH_SEPARATOR = '/';

    private ZKit zKit;

    private String ip;

    private BootstrapConfig.TcpConfig tcpConfig;

    public RegistryZK(ZKit zKit, String ip, BootstrapConfig.TcpConfig tcpConfig) {
        this.zKit = zKit;
        this.ip = ip;
        this.tcpConfig = tcpConfig;
    }

    @Override
    public void run() {
        zKit.createRootNode();

        String tcpPath = Constants.IM_CORE_ZK_ROOT + Constants.IM_CORE_ZK_ROOT_TCP + PATH_SEPARATOR +
                ip + ':' + tcpConfig.getTcpPort();
        zKit.createNode(tcpPath);
        LOGGER.info("Registry zookeeper tcpPath success, msg = [{}]", tcpPath);

        String webPath = Constants.IM_CORE_ZK_ROOT + Constants.IM_CORE_ZK_ROOT_WEB + PATH_SEPARATOR +
                ip + ':' + tcpConfig.getWebSocketPort();
        zKit.createNode(webPath);
        LOGGER.info("Registry zookeeper webPath success, msg = [{}]", webPath);
    }
}
