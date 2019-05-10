package com.wdy.module.config;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * 邮箱信证管理
 *
 * @author dongyang_wu
 * @date 2019/5/7 15:11
 */

public class MailTrustManager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] cert, String authType) {
        // everything is trusted
    }

    public void checkServerTrusted(X509Certificate[] cert, String authType) {
        // everything is trusted
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
