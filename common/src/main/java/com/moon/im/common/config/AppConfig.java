package com.moon.im.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Chanmoey
 * @date 2023年02月04日
 */
@Data
@Component
@ConfigurationProperties(prefix = "app-config")
public class AppConfig {

    private String zkAddr;

    private Integer zkConnectTimeOut;
}
