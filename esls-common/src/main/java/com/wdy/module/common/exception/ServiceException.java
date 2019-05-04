package com.wdy.module.common.exception;

import lombok.Data;

@Data
public class ServiceException extends RuntimeException {
    private Integer code;
    private String message;

    public ServiceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ServiceException(ResultEnum resultEnum) {
        this.message = resultEnum.getMessage();
        this.code = resultEnum.getCode();
    }

    public ServiceException(String message) {
        this.message = message;
        this.code = 0;
    }

    @Override
    public String toString() {
        return "错误码：" + code + " " + "错误信息：" + message;
    }
}
