package com.wdy.module.entity;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToLongConverter;
import lombok.*;

import java.io.Serializable;

/**
 * <p>
 * 验证码发送记录
 * </p>
 *
 * @author wdy
 * @since 2019-04-26
 */
@TableName("sms_verify")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SmsVerify extends Model<SmsVerify> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelField(title = "主键", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    /**
     * 短信编号（可以自己生成，也可以第三方复返回）
     */
    @TableField("smsId")
    @ExcelField(title = "短信ID", order = 2)
    private String smsId;
    /**
     * 电话号码
     */
    @TableField("mobile")
    @ExcelField(title = "手机号", order = 3)
    private String mobile;
    /**
     * 验证码
     */
    @TableField("smsVerify")
    @ExcelField(title = "验证码", order = 4)
    private String smsVerify;
    /**
     * 验证码类型（1：登录验证，2：注册验证，3：忘记密码，4：修改账号）
     */
    @TableField("smsType")
    @ExcelField(title = "验证码类型（1：登录验证，2：注册验证，3：忘记密码，4：修改账号）", order = 5, readConverter = StringToLongConverter.class)
    private Integer smsType;
    /**
     * 发送时间
     */
    @TableField("createTime")
    @ExcelField(title = "发送时间", order = 6, readConverter = StringToLongConverter.class)
    private Long createTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
