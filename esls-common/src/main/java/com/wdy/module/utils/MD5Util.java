package com.wdy.module.utils;


import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class MD5Util {
    public static int HASHITERATIONS = 3;

    public static String md5UserPassword(String password, String salt){
        ByteSource credentialsSalt = ByteSource.Util.bytes(salt);
        Object obj = new SimpleHash("MD5", password, credentialsSalt, MD5Util.HASHITERATIONS);
        return ((SimpleHash) obj).toHex();
    }
}
