package com.moon.im.common.route.algorithm.random;

import com.moon.im.common.enums.UserErrorCode;
import com.moon.im.common.exception.ApplicationException;
import com.moon.im.common.route.RouteHandle;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Chanmoey
 * @date 2023年02月04日
 */
public class RandomHandle implements RouteHandle {
    @Override
    public String routeServer(List<String> values, String key) {

        if (CollectionUtils.isEmpty(values)) {
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }

        int size = values.size();
        int idx = ThreadLocalRandom.current().nextInt(size);
        return values.get(idx);
    }
}
