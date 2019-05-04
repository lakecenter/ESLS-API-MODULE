package com.wdy.module.license;

import com.wdy.module.common.constant.LicenseConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 在项目启动时安装证书
 */
@Component("LicenseCheckListener")
public class LicenseCheckListener {
    private Logger logger = LogManager.getLogger(LicenseCheckListener.class);
    @Autowired
    private LicenseConstant licenseConstant;

    public void installLicense() {
        try {
            if (StringUtils.isNotBlank(licenseConstant.getLicensePath())) {
                logger.info("++++++++ 开始安装证书 ++++++++");
                LicenseVerifyParam param = new LicenseVerifyParam();
                param.setSubject(licenseConstant.getSubject());
                param.setPublicAlias(licenseConstant.getPublicAlias());
                param.setStorePass(licenseConstant.getStorePass());
                param.setLicensePath(licenseConstant.getLicensePath());
                param.setPublicKeysStorePath(licenseConstant.getPublicKeysStorePath());

                LicenseVerify licenseVerify = new LicenseVerify();
                //安装证书
                licenseVerify.install(param);

                logger.info("++++++++ 证书安装结束 ++++++++");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
