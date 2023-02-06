package com.moon.im.service.interceptor;

import com.alibaba.fastjson.JSON;
import com.moon.im.common.BaseErrorCode;
import com.moon.im.common.ResponseVO;
import com.moon.im.common.enums.GateWayErrorCode;
import com.moon.im.common.exception.ApplicationExceptionEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author Chanmoey
 * @date 2023年02月06日
 */
@Component
public class GateWayInterceptor implements HandlerInterceptor {

    @Autowired
    IdentityCheck identityCheck;


    //appService -》im接口 -》 userSign
    //appService（gen userSig）

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //获取appId 操作人 userSign
        String appIdStr = request.getParameter("appId");
        if (StringUtils.isBlank(appIdStr)) {
            resp(ResponseVO.errorResponse(GateWayErrorCode
                    .APPID_NOT_EXIST), response);
            return false;
        }

        String identifier = request.getParameter("identifier");
        if (StringUtils.isBlank(identifier)) {
            resp(ResponseVO.errorResponse(GateWayErrorCode
                    .OPERATER_NOT_EXIST), response);
            return false;
        }

        String userSign = request.getParameter("userSign");
        if (StringUtils.isBlank(userSign)) {
            resp(ResponseVO.errorResponse(GateWayErrorCode
                    .USER_SIGN_NOT_EXIST), response);
            return false;
        }

        //签名和操作人和appid是否匹配
        ApplicationExceptionEnum applicationExceptionEnum = identityCheck.checkUserSig(identifier,
                Long.valueOf(appIdStr), userSign);
        if (applicationExceptionEnum != BaseErrorCode.SUCCESS) {
            resp(ResponseVO.errorResponse(applicationExceptionEnum), response);
            return false;
        }

        return true;
    }


    private void resp(ResponseVO respVo, HttpServletResponse response) {

        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            String resp = JSON.toJSONString(respVo);
            writer = response.getWriter();
            writer.write(resp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.checkError();
            }
        }

    }
}
