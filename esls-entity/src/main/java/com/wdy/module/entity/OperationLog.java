package com.wdy.module.entity;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToIntegerConverter;
import com.wdy.module.converter.StringToLongConverter;
import lombok.*;

import java.io.Serializable;

/**
 * <p>
 * 操作日志表
 * </p>
 *
 * @author wdy
 * @since 2019-04-26
 */
@TableName("operation_log")
@Data
@ToString
@NoArgsConstructor
public class OperationLog extends Model<OperationLog> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelField(title = "id", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    /**
     * 日志描述
     */
    @TableField("logDescription")
    @ExcelField(title = "logDescription", order = 2)
    private String logDescription;
    /**
     * 方法参数
     */
    @TableField("actionArgs")
    @ExcelField(title = "actionArgs", order = 3)
    private String actionArgs;
    /**
     * 用户主键
     */
    @TableField("userName")
    @ExcelField(title = "userName", order = 4)
    private String userName;
    /**
     * 类名称
     */
    @TableField("className")
    @ExcelField(title = "className", order = 5)
    private String className;
    /**
     * 方法名称
     */
    @TableField("methodName")
    @ExcelField(title = "methodName", order = 6)
    private String methodName;
    @TableField("ip")
    @ExcelField(title = "ip", order = 7)
    private String ip;
    /**
     * 创建时间
     */
    @TableField("createTime")
    @ExcelField(title = "createTime", order = 8, readConverter = StringToLongConverter.class)
    private Long createTime;
    /**
     * 模块名称
     */
    @TableField("modelName")
    @ExcelField(title = "modelName", order = 9)
    private String modelName;
    /**
     * 操作
     */
    @TableField("action")
    @ExcelField(title = "action", order = 10)
    private String action;
    /**
     * 是否成功 1:成功 2异常
     */
    @TableField("succeed")
    @ExcelField(title = "succeed", order = 11, readConverter = StringToIntegerConverter.class)
    private Integer succeed;
    /**
     * 异常堆栈信息
     */
    @TableField("message")
    @ExcelField(title = "message", order = 12)
    private String message;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
