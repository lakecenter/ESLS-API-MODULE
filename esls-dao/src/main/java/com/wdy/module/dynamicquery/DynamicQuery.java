package com.wdy.module.dynamicquery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

/**
 * 扩展SpringDataJpa, 支持动态jpql/nativesql查询并支持分页查询
 * 使用方法：注入ServiceImpl
 */
public interface DynamicQuery {

    void save(Object entity);

    void update(Object entity);

    <T> void delete(Class<T> entityClass, Object entityid);

    <T> void delete(Class<T> entityClass, Object[] entityids);

    /**
     * 执行jpql查询一行
     *
     * @param resultClass 查询结果类型
     * @param jpql
     * @param params      占位符参数(例如?1)绑定的参数值
     * @return
     */
    <T> T querySingleResult(Class<T> resultClass, String jpql, @SuppressWarnings("rawtypes") Map paramsMap);

    /**
     * 执行jpql查询list集合
     *
     * @param resultClass 查询结果类型
     * @param jpql
     * @param params      占位符参数(例如?1)绑定的参数值
     * @return
     */
    <T> List<T> query(Class<T> resultClass, String jpql, @SuppressWarnings("rawtypes") Map paramsMap);

    /**
     * 执行jpql查询数量
     *
     * @param jpql
     * @param params 占位符参数(例如?1)绑定的参数值
     * @return 统计条数
     */
    Long queryCount(String jpql, Object... params);

    /**
     * 执行jpql的update,delete操作
     *
     * @param nativeSql
     * @param params
     * @return
     */
    int executeUpdate(String jpql, Object... params);

    /**
     * 执行nativeSql查询一行
     *
     * @param resultClass 查询结果类型
     * @param nativeSql
     * @param params      占位符参数(例如?1)绑定的参数值
     * @return
     */
    <T> T nativeQuerySingleResult(Class<T> resultClass, String nativeSql, Object... params);

    /**
     * 执行nativeSql查询一行(返回一个Object[]数组)
     *
     * @param nativeSql
     * @param params    占位符参数(例如?1)绑定的参数值
     * @return
     */
    <T> T nativeQuerySingleResult(String nativeSql, Object... params);

    /**
     * 执行nativeSql查询
     *
     * @param nativeSql
     * @param params    占位符参数(例如?1)绑定的参数值
     * @return
     */
    <T> List<T> query(String nativeSql, Object... params);

    /**
     * 执行nativeSql查询List<Object[]>
     *
     * @param nativeSql
     * @param params    占位符参数(例如?1)绑定的参数值
     * @return
     */
    <T> List<T> query(Class<T> resultClass, String nativeSql, Object... params);

    /**
     * 执行nativeSql分页查询
     *
     * @param resultClass 查询结果类型
     * @param pageable    分页数据
     * @param nativeSql
     * @param params      占位符参数(例如?1)绑定的参数值
     * @return 分页结果
     */
    <T> List<T> nativeQueryPagingList(Class<T> resultClass, Pageable pageable, String nativeSql, Object... params);

    /**
     * 执行nativeSql分页查询
     *
     * @param resultClass 查询结果类型
     * @param pageable    分页数据
     * @param nativeSql
     * @param params      占位符参数(例如?1)绑定的参数值
     * @return 分页对象
     */
    <T> Page<T> nativeQuery(Class<T> resultClass, Pageable pageable, String nativeSql, Object... params);

    /**
     * 执行nativeSql统计查询
     *
     * @param nativeSql
     * @param params    占位符参数(例如?1)绑定的参数值
     * @return 统计条数
     */
    Long nativeQueryCount(String nativeSql, Object... params);

    /**
     * 执行nativeSql的update,delete操作
     *
     * @param nativeSql
     * @param params
     * @return
     */
    int nativeExecuteUpdate(String nativeSql, Object... params);

    /**
     * 执行jpql查询
     *
     * @param resultClass             查询结果类型
     * @param 直接输入jpql参数，无需namedquery
     * @param params                  占位符参数(例如?1)绑定的参数值
     * @return
     */
    <T> List<T> directQuery(Class<T> resultClass, String jpql, Map<String, Object> paramsMap);


    /**
     * 执行jpql查询
     *
     * @param resultClass             查询结果类型
     * @param 直接输入jpql参数，无需namedquery
     * @param params                  占位符参数(例如?1)绑定的参数值
     * @return
     */
    <T> T directQuerySingleResult(Class<T> resultClass, String jpql, Map<String, Object> paramsMap);


    /**
     * 执行jpql分页查询
     *
     * @param resultClass 查询结果类型
     * @param pageable    分页数据
     * @param nativeSql
     * @param params      占位符参数(例如?1)绑定的参数值
     * @return 分页结果
     */
    <T> List<T> directQueryPagingList(Class<T> resultClass, Pageable pageable, String nativeSql, Map<String, Object> paramsMap);

    <T> TypedQuery<T> createTypedQueryByJpqlString(String qlString, Class<T> resultClass);

    /**
     * 查询对象列表，返回<组合对象>
     *
     * @param resultClass
     * @param nativeSql
     * @param params
     * @return T
     * @Date 2019年1月23日 更新日志
     * 2019年1月23日 张志朋  首次创建
     */
    <T> T nativeQueryModel(Class<T> resultClass, String nativeSql, Object... params);
}
