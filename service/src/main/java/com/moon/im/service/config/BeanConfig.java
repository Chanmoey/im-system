package com.moon.im.service.config;

import com.moon.im.common.config.AppConfig;
import com.moon.im.common.enums.ImUrlRouteWayEnum;
import com.moon.im.common.enums.RouteHashMethodEnum;
import com.moon.im.common.route.RouteHandle;
import com.moon.im.common.route.algorithm.consistenthash.AbstractConsistentHash;
import com.moon.im.common.route.algorithm.consistenthash.ConsistentHashHandle;
import com.moon.im.common.route.algorithm.consistenthash.TreeMapConsistentHash;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * @author Chanmoey
 * @date 2023年02月04日
 */
@Configuration
public class BeanConfig {

    @Autowired
    private AppConfig appConfig;

    @Bean
    public RouteHandle routeHandle() throws Exception {

        Integer imRouteWay = appConfig.getImRouteWay();
        String routeWay;

        ImUrlRouteWayEnum handler = ImUrlRouteWayEnum.getHandler(imRouteWay);
        routeWay = handler.getClazz();

        RouteHandle routeHandle = (RouteHandle) Class.forName(routeWay).getDeclaredConstructor().newInstance();

        if (handler == ImUrlRouteWayEnum.HASH) {
            Method setHash = Class.forName(routeWay).getMethod("setHash", AbstractConsistentHash.class);
            Integer consistentHashWay = appConfig.getConsistentHashWay();
            String hashWay;
            RouteHashMethodEnum hashHandler = RouteHashMethodEnum.getHandler(consistentHashWay);
            hashWay = hashHandler.getClazz();

            AbstractConsistentHash hashObject = (AbstractConsistentHash) Class.forName(hashWay).getDeclaredConstructor().newInstance();
            setHash.invoke(routeHandle, hashObject);
        }

        return routeHandle;
    }

    @Bean
    public ZkClient buildZkClient() {
        return new ZkClient(appConfig.getZkAddr(), appConfig.getZkConnectTimeOut());
    }
}
