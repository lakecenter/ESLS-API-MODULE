package com.wdy.module.license;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * License生成类需要的参数
 */
@Data
public class LicenseCreatorParam implements Serializable {

    private static final long serialVersionUID = -7793154252684580872L;
    /**
     * 证书subject
     */
    @JsonIgnore
    private String subject = "license_subject";

    /**
     * 密钥别称
     */
    @JsonIgnore
    private String privateAlias = "privateKey";

    /**
     * 密钥密码（需要妥善保管，不能让使用者知道）
     */
    @JsonIgnore
    private String keyPass = "private_password1234";

    /**
     * 访问秘钥库的密码
     */
    @JsonIgnore
    private String storePass = "public_password1234";

    /**
     * 证书生成路径
     */
    @JsonIgnore
    private String licensePath = "data.license";

    /**
     * 密钥库存储路径
     */
    @JsonIgnore
    private String privateKeysStorePath = "privateKeys.keystore";

    /**
     * 证书生效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date issuedTime = new Date();

    /**
     * 证书失效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expiryTime;

    /**
     * 用户类型
     */
    @JsonIgnore
    private String consumerType = "user";

    /**
     * 用户数量
     */
    @JsonIgnore
    private Integer consumerAmount = 1;

    /**
     * 描述信息
     */
    private String description = "";

    /**
     * 额外的服务器硬件校验信息
     */
    private LicenseCheckModel licenseCheckModel;


    @Override
    public String toString() {
        return "LicenseCreatorParam{" +
                "subject='" + subject + '\'' +
                ", privateAlias='" + privateAlias + '\'' +
                ", keyPass='" + keyPass + '\'' +
                ", storePass='" + storePass + '\'' +
                ", licensePath='" + licensePath + '\'' +
                ", privateKeysStorePath='" + privateKeysStorePath + '\'' +
                ", issuedTime=" + issuedTime +
                ", expiryTime=" + expiryTime +
                ", consumerType='" + consumerType + '\'' +
                ", consumerAmount=" + consumerAmount +
                ", description='" + description + '\'' +
                ", licenseCheckModel=" + licenseCheckModel +
                '}';
    }
}
