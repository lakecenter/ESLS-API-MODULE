package com.wdy.module.aop;


import java.lang.annotation.*;

/**
 * 在Controller方法上加入改注解会自动记录日志
 * @author : liugh
 * @date : 2018/05/08
 */
@Target( { ElementType.METHOD } )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface Log {

	/**
	 * 模块名称
	 */
	String modelName() default "";

	/**
	 * 操作
	 */
	String action()default "";
	/**
	 * 描述.
	 */
	String value() default "";

}
