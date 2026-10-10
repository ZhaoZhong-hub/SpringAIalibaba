package com.springAI.springaiquickstart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 绑定 application.properties 中 app.security.* 配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.security")
public class AppProperties {

    /** 默认登录用户名 */
    private String username;

    /** 默认登录密码 */
    private String password;
}
