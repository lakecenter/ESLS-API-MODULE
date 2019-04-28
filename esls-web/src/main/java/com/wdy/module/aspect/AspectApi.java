package com.wdy.module.aspect;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

/**
 * @author liugh 装饰器模式
 * @since on 2018/5/10.
 */
public interface AspectApi {

     Object doHandlerAspect(ProceedingJoinPoint pjp, Method method)throws Throwable;

}
