//package com.wdy.module.aop;
//
//import com.wdy.module.dao.LogDao;
//import com.wdy.module.entity.Logs;
//import com.wdy.module.entity.User;
//import com.wdy.module.serviceUtil.ContextUtil;
//import com.wdy.module.utils.RedisUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.aspectj.lang.reflect.CodeSignature;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import java.lang.reflect.Method;
//import java.sql.Timestamp;
//import java.util.*;
//
//@Component
//@Aspect
//@Slf4j
//public class WebLogAcpect {
//
//    @Autowired
//    private LogDao logDao;
//    @Autowired
//    private RedisUtil redisUtil;
//    ThreadLocal<Long> startTime = new ThreadLocal<>();
//    ThreadLocal<Logs> logger = new ThreadLocal<>();
//
//    //    @Pointcut("execution(public * com.datagroup.ESLS.controller..*.*(..))")
//    @Pointcut("@annotation(Log)")
//    public void weblog() {
//    }
//
//    @Before("weblog()")
//    public void doBefore(JoinPoint joinPoint) {
//        Logs logs = new Logs();
//        startTime.set(System.currentTimeMillis());
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//        String token = request.getHeader("ESLS");
//        if (token == null)
//            logs.setUsername("未登录或未授权用户");
//        else {
//            User admin = (User) redisUtil.sentinelGet(token, User.class);
//            if (admin != null)
//                ContextUtil.setUser(admin);
//            logs.setUsername(admin == null ? "token过期或无效" : admin.getName());
//        }
//        StringBuffer requestLog = new StringBuffer();
//        String str;
//        requestLog.append("请求信息：")
//                .append("URL = {" + request.getRequestURI() + "},\t")
//                .append("HTTP_METHOD = {" + request.getMethod() + "},\t")
//                .append("IP = {" + request.getRemoteAddr() + "},\t")
//                .append("CLASS_METHOD = {" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + "},\t");
//        if (joinPoint.getArgs().length == 0) {
//            // 获取post请求参数
//            str = getNameAndValue(request).toString();
//            requestLog.append("POST请求参数信息为 = " + str);
//        } else {
//            // 获取post请求参数
//            str = getNameAndValue(joinPoint).toString();
//            requestLog.append("GET请求参数信息为 = " + str + "");
//        }
//        if (str.length() > 100) logs.setParams("参数过长！");
//        else
//            logs.setParams(str);
//        logger.set(logs);
//        logs.setIp(request.getRemoteAddr());
//        logs.setCreateDate(new Timestamp(System.currentTimeMillis()));
//        log.info(requestLog.toString());
//    }
//
//    @AfterReturning(pointcut = "weblog()", returning = "ret")
//    public void doAfterReturning(JoinPoint joinPoint, Object ret) throws Throwable {
//        Logs logs = logger.get();
//        //从切面织入点处通过反射机制获取织入点处的方法
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        //获取切入点所在的方法
//        Method method = signature.getMethod();
//        //获取操作
//        Log myLog = method.getAnnotation(Log.class);
//        if (myLog != null) {
//            String value = myLog.value();
//            logs.setOperation(value);
//        }
//        //获取请求的类名
//        String className = joinPoint.getTarget().getClass().getName();
//        //获取请求的方法名
//        String methodName = method.getName();
//        logs.setMethod(className + "." + methodName);
//        // 处理完请求，返回内容
//        WebLogAcpect.log.info("响应结果: " + ret);
//        WebLogAcpect.log.info("执行时间 : " + (System.currentTimeMillis() - startTime.get()));
//        logs.setRunningTime(String.valueOf(System.currentTimeMillis() - startTime.get()));
////        Logs save = logDao.save(logs);
////        if (save != null)
////            log.info("日志记录成功！");
////        else
////            log.info("日志记录失败！");
//    }
//
//    @AfterThrowing(pointcut = "weblog()", throwing = "exception")
//    public void doAfterThrowing(JoinPoint joinPoint, Throwable exception) {
//        log.info("异常方法名 : " + joinPoint.getSignature().getName() + "  " + exception.toString());
//    }
//
//    /**
//     * 获取参数Map集合
//     *
//     * @param joinPoint
//     * @return
//     */
//    Map<String, Object> getNameAndValue(JoinPoint joinPoint) {
//        Map<String, Object> param = new HashMap<>();
//        Object[] paramValues = joinPoint.getArgs();
//        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
//        for (int i = 0; i < paramNames.length; i++)
//            param.put(paramNames[i], paramValues[i]);
//        return param;
//    }
//
//    Map<String, String> getNameAndValue(HttpServletRequest request) {
//        Enumeration<String> enumeration = request.getParameterNames();
//        Map<String, String> param = new HashMap<>();
//        while (enumeration.hasMoreElements()) {
//            String parameter = enumeration.nextElement();
//            param.put(parameter, request.getParameter(parameter));
//        }
//        return param;
//    }
//}
