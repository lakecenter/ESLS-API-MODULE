package com.wdy.module.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "响应对象")
public class ResultBean<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int SUCCESS = 1;

    public static final int FAIL = 0;

    public static final int NO_PERMISSION = 2;
    @ApiModelProperty(value = "响应的msg提示")
    private String msg = "success";
    @ApiModelProperty(value = "响应码 1成功 0失败 ")
    private int code = SUCCESS;
    @ApiModelProperty(value = "响应的json数据")
    private T data;

    public ResultBean() {
        super();
    }

    public ResultBean(T data) {
        super();
        this.data = data;
    }

    public ResultBean(T data, int code) {
        super();
        this.data = data;
        this.code = code;
    }

    public ResultBean(T data, String msg) {
        super();
        this.data = data;
        this.msg = msg;
    }

    public ResultBean(String msg, int code) {
        super();
        this.msg = msg;
        this.code = code;
    }

    public ResultBean(Throwable e) {
        super();
        this.msg = e.toString();
        this.code = FAIL;
    }

    public static ResultBean error(Object msg) {
        ResultBean error = new ResultBean(msg, ResultBean.FAIL);
        error.setMsg("error");
        return error;
    }

    public static ResultBean error(String msg, Integer code) {
        return new ResultBean(msg, code);
    }

    public static ResultBean success(Object msg) {
        return new ResultBean(msg);
    }
}