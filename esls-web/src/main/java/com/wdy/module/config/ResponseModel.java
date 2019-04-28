package com.wdy.module.config;

/**
 * 统一返回相应参数实体类
 *
 * @author liugh 53182347@qq.com
 */

import lombok.Data;
import lombok.ToString;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

@Data
@ToString
public class ResponseModel<T> implements Serializable {
    private static final long serialVersionUID = -1241360949457314497L;
    private T data;
    private String message;
    private String code;

    public ResponseModel() {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getResponse();
        response.setCharacterEncoding("UTF-8");
    }

}
