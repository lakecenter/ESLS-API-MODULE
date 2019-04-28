package com.wdy.module.config;

import org.springframework.http.HttpStatus;

/**
 * 统一返回相应参数
 *
 * @author liugh 53182347@qq.com
 */
public class ResponseHelper {

    public ResponseHelper() {
    }

    public static <T> ResponseModel<T> notFound(String message) {
        ResponseModel response = new ResponseModel();
        response.setCode(HttpStatus.NOT_FOUND.getReasonPhrase());
        response.setMessage(message);
        return response;
    }

    public static <T> ResponseModel<T> internalServerError(String message) {
        ResponseModel response = new ResponseModel();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        response.setMessage(message);
        return response;
    }

    public static <T> ResponseModel<T> validationFailure(String message) {
        ResponseModel response = new ResponseModel();
        response.setCode(HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.setMessage(message);
        return response;
    }

    public static <T> ResponseModel<T> buildResponseModel(T result) {
        ResponseModel response = new ResponseModel();
        response.setCode(HttpStatus.OK.getReasonPhrase());
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setData(result);
        return response;
    }
}
