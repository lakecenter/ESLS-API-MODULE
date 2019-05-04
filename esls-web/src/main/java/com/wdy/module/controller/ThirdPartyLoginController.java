package com.wdy.module.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.wdy.module.aop.Log;
import com.wdy.module.aop.Pass;
import com.wdy.module.config.ResourcesConfig;
import com.wdy.module.config.ThirdPartyLoginHelper;
import com.wdy.module.entity.User;
import com.wdy.module.entity.UserThirdparty;
import com.wdy.module.mybatis.mybatisModel.ThirdPartyUser;
import com.wdy.module.mybatis.mybatisService.IUserThirdpartyService;
import com.wdy.module.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by liugh on 2018/7/24.
 */
@Controller
@ApiIgnore
@RequestMapping("/thirdPartyLogin")
public class ThirdPartyLoginController {

    @Autowired
    private IUserThirdpartyService userThirdpartyService;

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     *
     * @param request
     * @param response
     * @param type     微博:sina  微信:wx  QQ:qq
     */
    @RequestMapping("/sns")
    @Log(action = "login", modelName = "thirdPartyLogin", value = "第三方用户登录")
    @Pass
    public void thirdLogin(HttpServletRequest request, HttpServletResponse response, @RequestParam("type") String type) {
        //拼接第三方登录授权地址
        String url = ThirdPartyLoginHelper.getRedirectUrl(request.getHeader("host"), type);
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * QQ登录回调
     *
     * @param request
     * @return
     */
    @GetMapping("/callback/qq")
    public void qqCallback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String host = request.getHeader("host");
        String code = request.getParameter("code");
        if (!StringUtils.isEmpty(code)) {// 如果不为空
            // 获取token和openid
            Map<String, String> map = ThirdPartyLoginHelper.getQQTokenAndOpenid(code, host);
            String openId = map.get("openId");
            if (!StringUtils.isEmpty(openId)) {// 如果openID存在
                // 利用access_token获取第三方用户信息
                ThirdPartyUser thirdUser = ThirdPartyLoginHelper.getQQUserinfo(map.get("access_token"), openId);
                thirdUser.setProvider("QQ");
                writeHrefHtml(request, response, thirdUser);
            } else {// 如果未获取到OpenID
                response.sendRedirect(ResourcesConfig.THIRDPARTY.getString("my_login"));
            }
        } else {// 如果没有返回令牌，则直接返回到登录页面
            response.sendRedirect(ResourcesConfig.THIRDPARTY.getString("my_login"));
        }
    }

    /**
     * 微信登录回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/callback/wx")
    public void wxCallback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String host = request.getHeader("host");
        String code = request.getParameter("code");
        if (!StringUtils.isEmpty(code)) {// 如果不为空
            // 获取token和openid
            Map<String, String> map = ThirdPartyLoginHelper.getWxTokenAndOpenid(code, host);
            String openId = map.get("openId");
            if (!StringUtils.isEmpty(openId)) {// 如果openID存在
                //利用access_token获取第三方用户信息
                ThirdPartyUser thirdUser = ThirdPartyLoginHelper.getWxUserinfo(map.get("access_token"), openId);
                thirdUser.setProvider("WX");
                writeHrefHtml(request, response, thirdUser);
            } else {// 如果未获取到OpenID
                response.sendRedirect(ResourcesConfig.THIRDPARTY.getString("my_login"));
            }
        } else {// 如果没有返回令牌，则直接返回到登录页面
            response.sendRedirect(ResourcesConfig.THIRDPARTY.getString("my_login"));
        }
    }


    /**
     * 微博登录回调
     * <p>
     * <p>
     * 第三方登录流程,自己实现,不用第三方SDK
     * 1.访问登录接口,根据类型拼接第三方授权url,重定向到该url上 url参数包括client_id,response_type,scope,redirect_uri信息等待用户登录
     * <p>
     * 2.用户确认登录后重定向到第一步带的redirect_uri并且给code赋值
     * <p>
     * 3.利用client_id、client_secret和code换取access_token和openid
     * <p>
     * 4.利用access_token和openid获取用户信息 如果获取到,则用HttpServletResponse写html页面window.location.href跳转到首页并带上自己生成的token
     * <p>
     * 5.使用获得的Access Token调用API  微博的access_token是有过期时间的，通常过期时间为30天
     */
    @GetMapping("callback/sina")
    public void sinaCallback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String host = request.getHeader("host");
        String code = request.getParameter("code");
        if (!StringUtils.isEmpty(code)) {
            //利用client_id、client_secret和code换取access_token和openid
            JSONObject json = ThirdPartyLoginHelper.getSinaTokenAndUid(code, host);
            String uid = json.getString("uid");//openid
            if (!StringUtils.isEmpty(uid)) {
                // 利用access_token获取第三方用户信息
                ThirdPartyUser thirdUser = ThirdPartyLoginHelper.getSinaUserinfo(json.getString("access_token"),
                        uid);
                thirdUser.setProvider("SINA");
                // 返回token
                writeHrefHtml(request, response, thirdUser);
            } else {// 如果未获取到OpenID
                response.sendRedirect(ResourcesConfig.THIRDPARTY.getString("my_login"));
            }
        } else {// 如果没有返回令牌，则直接返回到登录页面
            response.sendRedirect(ResourcesConfig.THIRDPARTY.getString("my_login"));
        }
    }


    @GetMapping("/cancel/callback/sina")
    @Pass
    public String sinaCancelCallback(HttpServletRequest request, ModelMap modelMap) {
        return ResourcesConfig.THIRDPARTY.getString("my_login");
    }


    private void writeHrefHtml(HttpServletRequest request, HttpServletResponse response, ThirdPartyUser thirdUser) throws Exception {
        Map<String, Object> stringObjectMap = thirdPartyLogin(thirdUser);
        User user = (User) stringObjectMap.get("user");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        //这里需要前端配合,把回传的自己系统的token写到header的Authentication字段里
        response.getWriter().write("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n" +
                "    <title>Document</title>\n" +
                "    <script>window.location.href='" + ResourcesConfig.THIRDPARTY.getString("login_success") + "?token=" + user.getAvatarUrl() + "'</script>\n" +
                "</head>\n" +
                "<body>\n" +
                "</body>\n" +
                "\n" +
                "</html>");
    }

    private Map<String, Object> thirdPartyLogin(ThirdPartyUser param) throws Exception {
        User sysUser;
        // 查询是否已经绑定过
        UserThirdparty userThirdparty = userThirdpartyService.selectOne(new EntityWrapper<UserThirdparty>()
                .where("provider_type = {0} and open_id = {1}", param.getProvider(), param.getOpenid()));
        if (StringUtils.isEmpty(userThirdparty)) {
            sysUser = userThirdpartyService.insertThirdPartyUser(param, "123456");
        } else {
            sysUser = userService.findByName(userThirdparty.getUserName());
        }
//        return userService.getLoginUserAndMenuInfo(sysUser);
        return null;
    }
}
