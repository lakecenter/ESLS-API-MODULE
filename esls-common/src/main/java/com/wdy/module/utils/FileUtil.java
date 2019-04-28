package com.wdy.module.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class FileUtil {
    // 存储文件
    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath + fileName);
        out.write(file);
        out.flush();
        out.close();
    }

    // 根据文件名删除文件
    public static boolean deleteFile(String filePath, String fileName) {
        boolean flag = false;
        File file = new File(filePath + fileName);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    // 判断文件是否存在
    public static boolean judeFileExists(String filePath, String fileName) {
        File file = new File(filePath + fileName);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static boolean createFileIfNotExist(String filePath, String fileName) {
        File file = new File(filePath + fileName);
        if (file.exists()) {
            return true;
        } else {
            file.mkdirs();
        }
        return false;
    }

    public static File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        File localFile;
        localFile = File.createTempFile("temp", null);
        multipartFile.transferTo(localFile);
        return localFile;
    }

    public static File creatTempFile() throws IOException {
        File localFile;
        localFile = File.createTempFile("temp", null);
        return localFile;
    }
}
