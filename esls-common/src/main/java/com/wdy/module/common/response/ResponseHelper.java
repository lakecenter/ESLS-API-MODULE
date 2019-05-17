package com.wdy.module.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 统一返回相应参数
 *
 * @author liugh 53182347@qq.com
 */
public class ResponseHelper {
    public final static int RESPONSE_OK = 1;
    public final static int RESPONSE_BADREQUEST = 2;

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

    public static <T> ResponseEntity<ResultBean> buildResultBean(T successResult, T failResult, Integer responseType) {
        switch (responseType) {
            case ResponseHelper.RESPONSE_OK:
                return ResponseEntity.ok(ResultBean.success(successResult));
            case ResponseHelper.RESPONSE_BADREQUEST:
                return ResponseEntity.badRequest().body(ResultBean.error(failResult));
            default:
                return ResponseEntity.ok(ResultBean.success(successResult));
        }
    }

    public static <T> ResponseEntity<ResultBean> BooleanResultBean(T successResult, T failResult, Boolean flag) {
        if (flag)
            return ResponseEntity.ok(ResultBean.success(successResult));
        else return ResponseEntity.badRequest().body(ResultBean.error(failResult));

    }

    public static <T> ResponseEntity<ResultBean> buildResultBean(T result, Integer responseType) {
        switch (responseType) {
            case ResponseHelper.RESPONSE_OK:
                return ResponseEntity.ok(ResultBean.success(result));
            case ResponseHelper.RESPONSE_BADREQUEST:
                return ResponseEntity.badRequest().body(ResultBean.error(result));
            default:
                return ResponseEntity.ok(ResultBean.success(result));
        }
    }

    public static <T> ResponseEntity<ResultBean> OK(T result) {
        return ResponseEntity.ok(ResultBean.success(result));
    }

    public static <T> ResponseEntity<ResultBean> BadRequest(T result) {
        return ResponseEntity.badRequest().body(ResultBean.error(result));
    }

    public static <T> ResponseEntity<ResultBean> OK(T result, Integer size) {
        return ResponseEntity.ok(new ResultBean(result, size));
    }

    public static <T> ResponseEntity<ResultBean> buildResultBean(T result, Integer size, Integer responseType) {
        switch (responseType) {
            case ResponseHelper.RESPONSE_OK:
                return ResponseEntity.ok(new ResultBean(result, size));
            case ResponseHelper.RESPONSE_BADREQUEST:
                return ResponseEntity.badRequest().body(new ResultBean(result, size));
            default:
                return ResponseEntity.ok(new ResultBean(result, size));
        }
    }
}
