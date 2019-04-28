package com.wdy.module.common.exception;

import com.wdy.module.common.response.ResultBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

/**
 * 自定义异常处理器
 * 
 */
@RestControllerAdvice
@Slf4j
@PropertySource("classpath:/ValidationMessages.properties")
public class DefaultExceptionHandler
{

    @Autowired
    private Environment environment;
    /**
     * 请求方式不支持
     */
    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    public ResponseEntity<ResultBean> handleException(HttpRequestMethodNotSupportedException e)
    {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(ResultBean.error("不支持' " + e.getMethod() + "'请求(未登录)"), HttpStatus.BAD_REQUEST);
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResultBean> notFount(RuntimeException e)
    {
        log.error("RuntimeException异常:", e);
        return new ResponseEntity<>(ResultBean.error(e.toString()), HttpStatus.BAD_REQUEST);
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultBean> handleException(Exception e)
    {
        log.error("Exception异常", e);
        return new ResponseEntity<>(ResultBean.error(e.toString()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,Object> handleBindException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        log.info("参数校验异常:{}({})", fieldError.getDefaultMessage(),fieldError.getField());
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("respCode", "01002");
        System.out.println(fieldError.getDefaultMessage());
        result.put("respMsg", environment.getProperty(fieldError.getDefaultMessage()));
        return result;
    }
    @ExceptionHandler(BindException.class)
    public Map<String,Object> handleBindException(BindException ex) {
        //校验 除了 requestbody 注解方式的参数校验 对应的 bindingresult 为 BeanPropertyBindingResult
        FieldError fieldError = ex.getBindingResult().getFieldError();
        log.info("必填校验异常:{}({})", fieldError.getDefaultMessage(),fieldError.getField());
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("respCode", "01002");
        result.put("respMsg", fieldError.getDefaultMessage());
        return result;
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResultBean> MissingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e){
        return new ResponseEntity<>(ResultBean.error("page字段和count字段必须同时提供("+e.getMessage()+")"), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResultBean> HttpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e){
        return new ResponseEntity<>(ResultBean.error("请注意参数类型！！如: [ swagger测试添加商品时去掉photo字段 ] [ list勿传请{}传null值 ] [勿传日期字段]("+e.getMessage()+")"), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResultBean> ConstraintViolationExceptionHandler(ConstraintViolationException e){
        if(e instanceof ConstraintViolationException) {
            HashMap<String, String> errors = new HashMap<>();
            Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
            for (ConstraintViolation<?> item : violations)
                errors.put(item.getPropertyPath().toString(),environment.getProperty(item.getMessage()));
            return new ResponseEntity<>(new ResultBean<>(errors,"参数错误"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ResultBean.error("SQLGrammarException请注意json中json字段的ID值！！如: [ JSON字段的ID值必须在数据库中存在 ] [ JSON字段传多个值时只有ID值生效 ] [ 建议JSON字段只传对于的ID值 ]("+e.toString()+")"), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(SQLGrammarException.class)
    public ResponseEntity<ResultBean> SQLGrammarExceptionHandler(SQLGrammarException e){
        return new ResponseEntity<>(ResultBean.error("SQL语句执行错误!! 如: [ 指定ID的数据在数据库是否存在 ] [ 是否多个标签关联了同一个商品 ] [ 建议选择属性进行标签商品绑定时 选择二者的唯一标识属性 ]("+e.toString()+")"), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResultBean> DataIntegrityViolationExceptionHandler(DataIntegrityViolationException e){
        return new ResponseEntity<>(ResultBean.error("SQL语句执行错误!! 如: [ 违背数据唯一性 barCode字段不允许重复 ] [ 指定ID的数据在数据库是否存在 ]("+e.toString()+")"), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResultBean> UnauthorizedExceptionHandler(UnauthorizedException e){
        return new ResponseEntity<>(ResultBean.error("权限不足("+e.toString()+")"), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ResultBean> TagServiceExceptionHandler(ServiceException serviceException){
        return new ResponseEntity<>(ResultBean.error(serviceException.getMessage(),serviceException.getCode()), HttpStatus.BAD_REQUEST);
    }
}
