package com.wdy.module.common.exception;

import lombok.Getter;

@Getter
public enum ResultEnum {
    // 成功
    SUCCESS(0,"成功"),
    COMMUNITICATION_ERROR(100000,"通信时Channel为空！路由器未连接"),
    FILE_ERROR(10000,"文件导入或导出失败"),

    USER_LOGIN_ERROR(20000,"用户登录认证失败"),
    TOKEN_INVALID(20001,"token无效或过期"),
    USER_NOT_EXIST(20002,"用户不存在"),
    USERNAME_OR_PASSWORD_ERROR(20003,"用户名或者密码错误"),
    USER_LOCKED(20004,"账号锁定"),
    USER_MAIL_ERROR(20005,"邮箱不合法"),
    USER_PHONE_ERROR(20006,"手机号码不合法"),
    USER_USERNAME_EMAIL_ERROR(20007,"用户名邮件一致"),
    USER_USERNAME_PHONE_ERROR(20008,"用户名手机号号码一致"),
    USER_EXIST(20009,"用户已经存在"),
    USER_NOT_LOGIN(20010,"用户未登录"),
    USER_COPY_USERVO_ERROR(20011,"user对象copy为uservo长度为0"),
    ORDER_NOT_EXIST(30000,"用户未授权"),
    ACTIVATE_EXPIRE(30001,"激活邮件过期"),
    VERTIFY_EXPIRE(30002,"登录验证码过期"),
    USER_SAVE_ERROR(30003,"用户存储失败"),
    GOOD_TAG_BIND_ERROR(40000,"商品标签绑定失败"),
    TAG_BINDED_GOOD_EMPTY(40001,"标签绑定商品为空"),
    TAG_NOT_EXIST(40002,"标签不存在"),
    STYLE_SEND_WORKING(50002,"发送样式正在进行中，指定秒内同一用户只允许调用指定次数发送样式接口(默认为15秒内1次可修改)"),
    CYCLEJOB_NOT_ALLOW_DELETE(60000,"扫描商品基本数据和扫描商品变价数据不允许删除"),
    CYCLEJOB_NOT_ALLOW_CHANGE_BASE_GOOD_JOB(60001,"扫描商品基本数据定时任务MODE和TYPE不匹配或ID为0"),
    CYCLEJOB_NOT_ALLOW_CHANGE_CHANGE_GOOD_JOB(60002,"扫描商品变价数据任务MODE和TYPE不匹配或ID为0"),
    ID_TO_INFO_NOT_EXIST(70000,"指定ID数据不存在"),
    TAG_BIND_ROUTER_NOT_EXIST(80000,"标签绑定路由器不存在"),
    ROUTER_BIND_SHOP_NOT_EXIST(80000,"路由器绑定店铺不存在"),
    MESSAGE_SEND_ERROR(90000,"短信下发失败"),
    VERIFY_PARAM_ERROR(90001,"短信校验码错误"),
    VERIFY_PARAM_PASS(90002,"短信校验码过期"),
    SERVER_BUSY(100000,"服务器繁忙，请稍后再试!"),
    ;
    private Integer code;
    private String message;
    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
