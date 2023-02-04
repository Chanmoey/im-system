package com.moon.im.common.route.algorithm.loop;

import com.moon.im.common.enums.UserErrorCode;
import com.moon.im.common.exception.ApplicationException;
import com.moon.im.common.route.RouteHandle;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Chanmoey
 * @date 2023年02月04日
 */
public class LoopHandle implements RouteHandle {

    private final AtomicLong index = new AtomicLong();

    @Override
    public String routeServer(List<String> values, String key) {

        if (CollectionUtils.isEmpty(values)) {
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }

        int size = values.size();
        int idx = (int) (index.incrementAndGet() % size);
        return values.get(idx);
    }
}
