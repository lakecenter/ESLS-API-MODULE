package com.wdy.module.license;

import com.alibaba.fastjson.JSON;
import com.wdy.module.common.constant.RedisConstant;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.entity.User;
import com.wdy.module.serviceUtil.ContextUtil;
import com.wdy.module.serviceUtil.LicenseUtil;
import com.wdy.module.serviceUtil.SpringContextUtil;
import com.wdy.module.utils.RedisUtil;
import org.bouncycastle.util.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;

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
            RedisUtil redisUtil = (RedisUtil) SpringContextUtil.getBean("RedisUtil");
            String token = request.getHeader(ContextUtil.AUTHORIZATION);
            Object o = redisUtil.get(token + RedisConstant.LICENSESUFFIX);
            if (o != null) {
                // 缓存不为空 更新缓存过期时间
                long end = LicenseUtil.licenseContent.getNotAfter().getTime();
                redisUtil.set(token + RedisConstant.LICENSESUFFIX, token, end - System.currentTimeMillis());
                return true;
            }
            //校验证书是否有效
            boolean verifyResult = licenseVerify.verify();
            if (verifyResult) {
                long end = LicenseUtil.licenseContent.getNotAfter().getTime();
                redisUtil.set(token + RedisConstant.LICENSESUFFIX, token, end - System.currentTimeMillis());
                return true;
            } else {
                response.setCharacterEncoding("utf-8");
                response.setStatus(401);
                response.setContentType("text/html");
                response.getWriter().write(JSON.toJSONString(ResultBean.error("您的证书无效，请核查服务器是否取得授权或重新申请证书！")));
                return false;
            }
        } catch (Exception e) {
            response.setCharacterEncoding("utf-8");
            response.setStatus(401);
            response.setContentType("text/html");
            response.getWriter().write(JSON.toJSONString(ResultBean.error("证书未安装")));
        }
        return false;
    }

}
