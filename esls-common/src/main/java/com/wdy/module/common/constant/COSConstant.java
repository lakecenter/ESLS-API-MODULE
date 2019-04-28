package com.wdy.module.common.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cos")
@Data
public class COSConstant {
    private String secretId ;
    private String secretKey ;
    private String region ;
    private String bucketName ;
    private String path;
}
