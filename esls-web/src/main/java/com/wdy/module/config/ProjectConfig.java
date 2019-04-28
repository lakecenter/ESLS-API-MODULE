package com.wdy.module.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 * 
 *
 */
@Component
@ConfigurationProperties(prefix = "project")
@Data
public class ProjectConfig
{
    /** 项目名称 */
    private String name;
    /** 版本 */
    private String version;
    /** 上传路径 */
    private static String profile;
    public static String getAvatarPath()
    {
        return profile + "avatar/";
    }
    public static String getDownloadPath()
    {
        return profile + "download/";
    }
}
