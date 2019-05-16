package com.wdy.module.controller;

import com.wdy.module.common.response.ResponseHelper;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.license.*;
import com.wdy.module.serviceUtil.SpringContextUtil;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
@Api(description = "证书API")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LicenseCreatorController {


    @ApiOperation("获取服务器硬件信息")
    @GetMapping(value = "/license/getServerInfos", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public LicenseCheckModel getServerInfos(@RequestParam(value = "osName", required = false) String osName) {
        //操作系统类型
        if (StringUtils.isBlank(osName)) {
            osName = System.getProperty("os.name");
        }
        osName = osName.toLowerCase();

        AbstractServerInfos abstractServerInfos = null;

        //根据不同操作系统类型选择不同的数据获取方法
        if (osName.startsWith("windows")) {
            abstractServerInfos = new WindowsServerInfos();
        } else if (osName.startsWith("linux")) {
            abstractServerInfos = new LinuxServerInfos();
        } else {//其他服务器类型
            abstractServerInfos = new LinuxServerInfos();
        }

        return abstractServerInfos.getServerInfos();
    }

    //        {
//        "subject": "license_demo",
//            "privateAlias": "privateKey",
//            "keyPass": "private_password1234",
//            "storePass": "public_password1234",
//            "licensePath": "F:/csvData/data.license",
//            "privateKeysStorePath": "F:/csvData/privateKeys.keystore",
//            "issuedTime": "2018-07-10 00:00:01",
//            "expiryTime": "2019-12-31 23:59:59",
//            "consumerType": "User",
//            "consumerAmount": 1,
//            "description": "这是证书描述信息",
//            "licenseCheckModel": {
//        "ipAddress": ["192.168.245.1", "10.0.5.22"],
//        "macAddress": ["00-50-56-C0-00-01", "50-7B-9D-F9-18-41"],
//        "cpuSerial": "BFEBFBFF000406E3",
//                "mainBoardSerial": "L1HF65E00X9"
//    }
//    }
    @ApiOperation("生成证书")
    @PostMapping(value = "/license/generateLicense", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<ResultBean> generateLicense(@RequestBody LicenseCreatorParam param) {
        LicenseCreator licenseCreator = new LicenseCreator(param);
        boolean result = licenseCreator.generateLicense();
        return ResponseHelper.OK(result);
    }

    @ApiOperation("下载证书")
    @GetMapping(value = "/license/downloadLicense")
    public ResponseEntity<ResultBean> downloadLicense(HttpServletResponse response) {
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + "data.license");// 设置文件名
        // 写出响应
        try (OutputStream os = response.getOutputStream()) {
//            ClassPathResource classPathResource = new ClassPathResource("license/data.license");
            File file = new File("data.license");
            byte[] buffer = new byte[1024];
            FileInputStream fis;
            BufferedInputStream bis;
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }

            return ResponseHelper.OK("下载成功");
        } catch (Exception e) {
            return ResponseHelper.BadRequest("下载失败" + e);
        }
    }

    @ApiOperation("为后台安装证书")
    @PostMapping("/license/installLicense")
    public ResponseEntity<ResultBean> installLicense(@ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile multipartFile) {
        try {
//            File licenseFile = File.createTempFile("licenseFile", null);
//            multipartFile.transferTo(licenseFile);
//            FileOutputStream os = new FileOutputStream(new ClassPathResource("license/localData.license").getFile());
//            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(licenseFile));
//            byte[] buffer = new byte[1024];
//            int i = bis.read(buffer);
//            while (i != -1) {
//                os.write(buffer, 0, i);
//                i = bis.read(buffer);
//            }
            String property = System.getProperty("user.dir");
            File localFile = new File(property + File.separator + "localData.license");
            multipartFile.transferTo(localFile);
            LicenseCheckListener licenseCheckListener = (LicenseCheckListener) SpringContextUtil.getBean("LicenseCheckListener");
            licenseCheckListener.installLicense();
            return ResponseHelper.OK("安装成功");
        } catch (Exception e) {
            return ResponseHelper.BadRequest("安装失败" + e);
        }
    }

    @ApiOperation("获取安装的证书相关信息")
    @GetMapping("/license")
    public ResponseEntity<ResultBean> getValidTime() throws Exception {
        LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
        LicenseContent verify = licenseManager.verify();
        return ResponseHelper.OK(verify);
    }
}
