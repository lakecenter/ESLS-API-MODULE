package com.wdy.module.serviceUtil;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import com.wdy.module.common.constant.COSConstant;
import com.wdy.module.utils.FileUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class COSUtil {
    public static String PutObjectRequest(MultipartFile file, String key) throws IOException {
        COSConstant cosConstant = (COSConstant) SpringContextUtil.getBean("COSConstant");
        // 1 初始化用户身份信息（secretId, secretKey）。
        COSCredentials cred = new BasicCOSCredentials(cosConstant.getSecretId(), cosConstant.getSecretKey());
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(cosConstant.getRegion()));
        // 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);
        // bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
        String bucketName = cosConstant.getBucketName();
        File localFile = FileUtil.multipartFileToFile(file);
        // 指定要上传到 COS 上对象键
        // String key = "goods_image/"+fileName;
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
        cosClient.putObject(putObjectRequest);
        return cosConstant.getPath() + key;
    }
}
