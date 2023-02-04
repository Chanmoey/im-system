package com.moon.im.common.util;

import com.moon.im.common.BaseErrorCode;
import com.moon.im.common.enums.CommonErrorCode;
import com.moon.im.common.exception.ApplicationException;
import com.moon.im.common.route.RouteInfo;

/**
 * @author Chanmoey
 * @date 2023年02月04日
 */
public class RouteInfoParseUtil {

    private RouteInfoParseUtil(){}

    public static RouteInfo parse(String info){
        try {
            String[] serverInfo = info.split(":");
            if (serverInfo.length != 2) {
                throw new ApplicationException(CommonErrorCode.ERROR_IP_ADDRESS);
            }
            return new RouteInfo(serverInfo[0], Integer.parseInt(serverInfo[1]));
        }catch (Exception e){
            throw new ApplicationException(BaseErrorCode.PARAMETER_ERROR) ;
        }
    }
}
