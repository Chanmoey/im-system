package com.moon.im.common.route;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年02月04日
 */
public interface RouteHandle {

    /**
     * 根据key获取一个im服务地址
     */
    String routeServer(List<String> values, String key);
}
