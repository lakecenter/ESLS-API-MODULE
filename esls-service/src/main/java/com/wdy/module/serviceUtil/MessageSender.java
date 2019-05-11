package com.wdy.module.serviceUtil;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.wdy.module.common.exception.ResultEnum;
import com.wdy.module.common.exception.ServiceException;
import com.wdy.module.entity.*;
import com.wdy.module.utils.MailSender;
import com.wdy.module.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
public class MessageSender {
    private static final String accessKeyId = "LTAIFPXGzVz1u5HT";
    private static final String accessKeySecret = "H2UinkHcpw8ZC1R8QVcpcID7OS4ei6";
    private static final String product = "Dysmsapi";
    private static final String domain = "dysmsapi.aliyuncs.com";

    public static String sendMsgByTxPlatform(String phone, String[] params) throws Exception {
        // 短信应用SDK AppID
        // 1400开头
        int appId = 1400202313;
        // 短信应用SDK AppKey
        String appKey = "a79650f35ce2178bb890502edba51ce5";
        // 需要发送短信的手机号码
        // String[] phoneNumbers = {"15212111830"};
        // 短信模板ID，需要在短信应用中申请
        //NOTE: 这里的模板ID`7839`只是一个示例，真实的模板ID需要在短信控制台中申请
        int templateId = 313293;
        // 签名
        // NOTE: 这里的签名"腾讯云"只是一个示例，真实的签名需要在短信控制台中申请，另外签名参数使用的是`签名内容`，而不是`签名ID`
        String smsSign = "ESLS后台管理系统";
        SmsSingleSender sSender = new SmsSingleSender(appId, appKey);
        // 签名参数未提供或者为空时，会使用默认签名发送短信
        SmsSingleSenderResult result = sSender.sendWithParam("86", phone,
                templateId, params, "", "", "");
        RedisUtil redisUtil = (RedisUtil) SpringContextUtil.getBean("RedisUtil");
        if (result.result == 0)
            redisUtil.sentinelSet(phone, params[0], (long) (60000 * 5));
        return result.result == 0 ? "发送成功" : "发送失败";
    }

    public static String[] getCodeAndTime() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 4; i++)
            sb.append((int) (1 + Math.random() * 10));
        sb.append(" 5");
        return sb.toString().split(" ");
    }

    public static String generateRandomCode() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 4; i++)
            sb.append((int) (1 + Math.random() * 10));
        return sb.toString();
    }

    public static boolean sendAuthCode(String phone) {
        String authcode = generateRandomCode();
        if (phone != null)
            return true;
        try {
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            // 组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            // 使用post提交
            request.setMethod(MethodType.POST);
            // 必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为国际区号+号码，如“85200000000”
            request.setPhoneNumbers(phone);
            // 必填:短信签名-可在短信控制台中找到
            request.setSignName("starfire星火");
            // 必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
            request.setTemplateCode("SMS_149100481");
            // 可选:模板中的变量替换JSON串,如模板内容为"您的验证码为${code}"时,此处的值为
            // 友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            request.setTemplateParam("{\"code\":\"" + authcode + "\"}");
            // 可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
            // request.setSmsUpExtendCode("90997");
            // 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
            // request.setOutId("");
            // 请求失败这里会抛ClientException异常
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                RedisUtil redisUtil = (RedisUtil) SpringContextUtil.getBean("RedisUtil");
                redisUtil.sentinelSet(phone, authcode, (long) (60000 * 5));
                return true;
            }
        } catch (ClientException e) {
            log.error("CLIENT EXCEPTION: CODE:" + e.getErrCode() + "\tMSG:" + e.getErrMsg());
        }
        return false;
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

    public static void sendGoodUpdateMessage(List<Tag> tags, List<Tag> nosuccessTags) {
        try {
            List<User> tos = MessageSender.getUsersByTags(tags);
            StringBuffer sb = new StringBuffer();
            sb.append("总数:" + tags.size() + "\r\n" + tags.toString()).append("\r\n").append("失败:" + nosuccessTags.size() + "\r\n" + nosuccessTags.toString());
            MailSender mailSender = (MailSender) SpringContextUtil.getBean("MailSender");
            for (User user : tos) {
                System.out.println("向" + user.getName() + "发送邮件");
                mailSender.sendSSLMail(user.getMail(), "ESLS变价通知信息邮件", sb.toString());
            }
        } catch (Exception e) {
            log.info("发送ESLS变价通知信息邮件失败" + e);
        }
    }
}
