package com.wdy.module.common.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @program: esls-parent
 * @description:
 * @author: dongyang_wu
 * @create: 2019-04-30 23:03
 */
@Component
@ConfigurationProperties(prefix = "license")
@Data
public class LicenseConstant {
    private String subject;
    private String publicAlias;
    private String storePass;
    private String licensePath;
    private String publicKeysStorePath;
}