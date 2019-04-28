package com.wdy.module.license;

import com.alibaba.fastjson.JSON;
import com.wdy.module.common.response.ResultBean;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * LicenseCheckInterceptor
 *
 * @author dongyang_wu
 * @date 2019/4/16
 * @since 1.0.0
 */
public class LicenseCheckInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LicenseVerify licenseVerify = new LicenseVerify();
        try {
            //校验证书是否有效
            boolean verifyResult = licenseVerify.verify();
            if (verifyResult) {
                return true;
            } else {
                response.setCharacterEncoding("utf-8");
                response.setStatus(401);
                response.getWriter().write(JSON.toJSONString(ResultBean.error("您的证书无效，请核查服务器是否取得授权或重新申请证书！")));
                return false;
            }
        } catch (Exception e) {
            response.setCharacterEncoding("utf-8");
            response.setStatus(401);
            response.getWriter().write(JSON.toJSONString(ResultBean.error("证书未安装")));
        }
        return false;
    }

}
