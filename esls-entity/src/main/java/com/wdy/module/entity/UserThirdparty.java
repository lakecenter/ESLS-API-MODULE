package com.wdy.module.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToIntegerConverter;
import com.wdy.module.converter.StringToLongConverter;
import lombok.*;

import java.io.Serializable;

/**
 * <p>
 * 第三方用户表
 * </p>
 *
 * @author liugh123
 * @since 2018-07-27
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("user_thirdparty")
public class UserThirdparty extends Model<UserThirdparty> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelField(title = "主键", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    /**
     * 第三方Id
     */
    @TableField("openId")
    @ExcelField(title = "第三方Id", order = 2)
    private String openId;
    /**
     * 绑定用户的id
     */
    @TableField("userName")
    @ExcelField(title = "绑定用户名", order = 3)
    private String userName;
    /**
     * 第三方token
     */
    @TableField("accessToken")
    @ExcelField(title = "第三方token", order = 4)
    private String accessToken;
    /**
     * 第三方类型 qq:QQ 微信:WX 微博:SINA
     */
    @TableField("providerType")
    @ExcelField(title = "第三方类型 qq:QQ 微信:WX 微博:SINA", order = 5)
    private String providerType;
    /**
     * 状态值（1：启用，2：禁用，3：删除）
     */
    @TableField("status")
    @ExcelField(title = "状态值（1：启用，2：禁用，3：删除）", order = 6, readConverter = StringToIntegerConverter.class)
    private Integer status;
    /**
     * 创建时间
     */
    @TableField("createTime")
    @ExcelField(title = "创建时间", order = 7, readConverter = StringToLongConverter.class)
    private Long createTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
