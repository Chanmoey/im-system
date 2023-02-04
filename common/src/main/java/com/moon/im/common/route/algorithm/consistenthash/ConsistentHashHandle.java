package com.moon.im.common.route.algorithm.consistenthash;

import com.moon.im.common.route.RouteHandle;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2023年02月04日
 */
public class ConsistentHashHandle implements RouteHandle {

    private AbstractConsistentHash hash;

    public void setHash(AbstractConsistentHash hash) {
        this.hash = hash;
    }

    @Override
    public String routeServer(List<String> values, String key) {
        return hash.process(values, key);
    }
}
