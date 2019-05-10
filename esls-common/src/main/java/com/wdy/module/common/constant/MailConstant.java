package com.wdy.module.common.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信社区端服务
 *
 * @author dongyang_wu
 * @date 2019/4/29 14:30
 */
@Component
@ConfigurationProperties(prefix = "mail")
@Data
public class MailConstant {
    private String HOST;
    private String PROTOCOL;
    private String FROM;
    private String PASSWORD;
    private String DEFAULTENCODING;
}
