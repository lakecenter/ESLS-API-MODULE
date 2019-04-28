package com.wdy.module.controller;

import com.wdy.module.common.ResultBean;
import com.wdy.module.license.LicenseCreator;
import com.wdy.module.license.LicenseCreatorParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

@RestController
@RequestMapping("/license")
@Api(description = "生成证书API")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LicenseCreatorController {

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
    @PostMapping(value = "/generateLicense", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<ResultBean> generateLicense(@RequestBody LicenseCreatorParam param, @RequestParam String userName, @RequestParam String passWd) {
        boolean result =false;
        if(!StringUtils.isEmpty(userName)  && !StringUtils.isEmpty(passWd) && "ESLS".equals(userName)  && "123456".equals(passWd)) {
            LicenseCreator licenseCreator = new LicenseCreator(param);
            result = licenseCreator.generateLicense();
        }

        return new ResponseEntity<>(ResultBean.success(result), HttpStatus.OK);
    }

    @ApiOperation("下载证书")
    @GetMapping(value = "/downloadLicense")
    public ResponseEntity<ResultBean> downloadLicense(HttpServletResponse response) {
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + "data.license");// 设置文件名
        // 写出响应
        try (OutputStream os = response.getOutputStream()) {
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
            return new ResponseEntity<>(ResultBean.success("下载成功"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(ResultBean.error("下载失败"+e), HttpStatus.BAD_REQUEST);
        }
    }

}
