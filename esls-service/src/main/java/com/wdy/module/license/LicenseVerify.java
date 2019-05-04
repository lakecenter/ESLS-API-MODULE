package com.wdy.module.license;

import com.wdy.module.serviceUtil.LicenseUtil;
import de.schlichtherle.license.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.text.*;
import java.util.prefs.Preferences;

/**
 * License校验类
 */
public class LicenseVerify {
    private static Logger logger = LogManager.getLogger(LicenseVerify.class);

    /**
     * 安装License证书
     *
     * @param [param]
     * @return de.schlichtherle.license.LicenseContent
     * @throws
     * @author dongyang_wu
     * @date 2019/4/16 10:44
     */
    public synchronized LicenseContent install(LicenseVerifyParam param) throws Exception {
        LicenseContent result;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //1. 安装证书
        LicenseManager licenseManager = LicenseManagerHolder.getInstance(initLicenseParam(param));
        licenseManager.uninstall();
//        File localFile = File.createTempFile("temp", null);
//        FileUtils.copyInputStreamToFile(new ClassPathResource(param.getLicensePath()).getInputStream(), localFile);
        File localFile = new File(param.getLicensePath());
        result = licenseManager.install(localFile);
        LicenseUtil.licenseContent = result;
        logger.info(MessageFormat.format("证书安装成功，证书有效期：{0} - {1}", format.format(result.getNotBefore()), format.format(result.getNotAfter())));

        return result;
    }

    /**
     * 校验License证书
     *
     * @param []
     * @return boolean
     * @throws
     * @author dongyang_wu
     * @date 2019/4/16 10:44
     */
    public boolean verify() {
        LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //2. 校验证书
        try {
            LicenseContent licenseContent = licenseManager.verify();
//            System.out.println(licenseContent.getSubject());

            logger.info(MessageFormat.format("证书校验通过，证书有效期：{0} - {1}", format.format(licenseContent.getNotBefore()), format.format(licenseContent.getNotAfter())));
            return true;
        } catch (Exception e) {
            logger.error("证书校验失败！", e);
            return false;
        }
    }

    /**
     * 初始化证书生成参数
     *
     * @param [param]
     * @return de.schlichtherle.license.LicenseParam
     * @throws
     * @author dongyang_wu
     * @date 2019/4/16 10:44
     */
    private LicenseParam initLicenseParam(LicenseVerifyParam param) {
        Preferences preferences = Preferences.userNodeForPackage(LicenseVerify.class);

        CipherParam cipherParam = new DefaultCipherParam(param.getStorePass());

        KeyStoreParam publicStoreParam = new CustomKeyStoreParam(LicenseVerify.class
                , param.getPublicKeysStorePath()
                , param.getPublicAlias()
                , param.getStorePass()
                , null);

        return new DefaultLicenseParam(param.getSubject()
                , preferences
                , publicStoreParam
                , cipherParam);
    }

}
