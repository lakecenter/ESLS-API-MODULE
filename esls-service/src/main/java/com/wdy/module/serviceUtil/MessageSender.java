package com.wdy.module.serviceUtil;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.wdy.module.common.exception.ResultEnum;
import com.wdy.module.common.exception.ServiceException;
import com.wdy.module.entity.*;
import com.wdy.module.utils.RedisUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class MessageSender {
    public static String sendMsgByTxPlatform(String phone) throws Exception {
        // 短信应用SDK AppID
        // 1400开头
        int appId = 1400196574;
        // 短信应用SDK AppKey
        String appKey = "6f189199b34f5358883bc0e22e1faeae";
        // 需要发送短信的手机号码
        // String[] phoneNumbers = {"15212111830"};
        // 短信模板ID，需要在短信应用中申请
        //NOTE: 这里的模板ID`7839`只是一个示例，真实的模板ID需要在短信控制台中申请
        int templateId = 305427;
        String[] params = getCodeAndTime();
        // 签名
        // NOTE: 这里的签名"腾讯云"只是一个示例，真实的签名需要在短信控制台中申请，另外签名参数使用的是`签名内容`，而不是`签名ID`
        String smsSign = "ESLS后台管理系统";
        SmsSingleSender sSender = new SmsSingleSender(appId, appKey);
        // 签名参数未提供或者为空时，会使用默认签名发送短信
        SmsSingleSenderResult result = sSender.sendWithParam("86", phone,
                templateId, params, smsSign, "", "");
        System.out.println(result);
        RedisUtil redisUtil = (RedisUtil) SpringContextUtil.getBean("RedisUtil");
        if (result.result > 0)
            redisUtil.sentinelSet(phone, params[0], (long) (60000 * 5));
        return result.result == 0 ? "发送失败" : "发送成功";
    }

    public static String[] getCodeAndTime() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 4; i++)
            sb.append((int) (1 + Math.random() * 10));
        sb.append(" 5");
        return sb.toString().split(" ");
    }

    public static void sendMessageByApi() {
        HttpRequestUtils httpRequestUtils = (HttpRequestUtils) SpringContextUtil.getBean("httpRequestUtils");
        String url = "https://sms.yunpian.com/v2/sms/single_send.json";
        Map<String, Object> params = new HashMap<>();
        params.put("apikey", "4202de952b80c5895e4ebe46cd926b1b");
        params.put("text", "文本");
        params.put("mobile", "17722828134");
        ResponseEntity result = httpRequestUtils.doHttpPostMethod(url, getHttpHeaders(), params);
        System.out.println(result);
    }

    private static Map getHttpHeaders() {
        HashMap<String, Object> headers = new HashMap<>();
        headers.put("Accept", "application/json;charset=utf-8");
        headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        return headers;
    }

    public static List<User> getUsersByTags(List<Tag> tags) {
        List<User> users = new ArrayList<>();
        for (Tag tag : tags) {
            Router router = tag.getRouter();
            if (router == null)
                throw new ServiceException(ResultEnum.TAG_BIND_ROUTER_NOT_EXIST);
            Shop shop = router.getShop();
            if (shop == null)
                throw new ServiceException(ResultEnum.ROUTER_BIND_SHOP_NOT_EXIST);
            List<User> needToSending = (List) shop.getUsers();
            if (!CollectionUtils.isEmpty(needToSending))
                users.addAll(needToSending);
        }
        return users;
    }
}
