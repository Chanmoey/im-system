package com.moon.im.service.config;

import com.moon.im.common.config.AppConfig;
import com.moon.im.common.route.RouteHandle;
import com.moon.im.common.route.algorithm.random.RandomHandle;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Chanmoey
 * @date 2023年02月04日
 */
@Configuration
public class BeanConfig {

    @Autowired
    private AppConfig appConfig;

    @Bean
    public RouteHandle routeHandle() {
        return new RandomHandle();
    }

    @Bean
    public ZkClient buildZkClient() {
        return new ZkClient(appConfig.getZkAddr(), appConfig.getZkConnectTimeOut());
    }
}
