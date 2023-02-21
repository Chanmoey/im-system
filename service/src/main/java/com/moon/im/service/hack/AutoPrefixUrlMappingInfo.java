package com.moon.im.service.hack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * @author Chanmoey
 * @date 2023年01月29日
 */
public class AutoPrefixUrlMappingInfo extends RequestMappingHandlerMapping {

    @Value("${im-system.api-package}")
    private String controllerPackagePath;

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo mappingInfo = super.getMappingForMethod(method, handlerType);
        if (mappingInfo != null) {
            String prefix = this.getPrefix(handlerType);
            return RequestMappingInfo.paths(prefix).build().combine(mappingInfo);
        }
        return null;
    }

    private String getPrefix(Class<?> handlerType) {
        String packageName = handlerType.getPackage().getName();
        String dotPath = packageName.replaceAll(controllerPackagePath, "");
        if ("".equals(dotPath)) {
            return "/";
        }
        return dotPath.replace(".", "/");
    }
}
