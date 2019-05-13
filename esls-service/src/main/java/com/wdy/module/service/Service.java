
package com.wdy.module.service;


import com.wdy.module.common.request.QueryAllBean;
import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.response.ResultBean;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface Service<T> {
    List findBySql(String s);

    List findBySql(String s, Class clazz);

    List findAllBySql(String table, String query, String queryString, Integer page, Integer count, Class clazz);

    List findByArrtribute(String table, String query, String queryString, Class clazz);

    List findAllBySql(String table, String query, String queryString, Class clazz);

    Integer updateByArrtribute(String table, RequestBean source, RequestBean target);

    Integer deleteByIdList(String table, String query, List<Long> idList);

    // 根据传入的多属性进行搜索
    List findAllBySql(String table, String connection, RequestBean requestBean, Integer page, Integer count, Class clazz);

    // 根据传入的属性进行或搜索
    List findAllBySql(String table, String connection, String query, String queryString, Integer page, Integer count, Class clazz);

    // 通用方法
    void save(T bean);

    void update(T bean);

    T find(Long id);

    int delete(Long id);

    List<T> findAlls();

    List<T> findAlls(Integer page, Integer pageSize);

    void refreshSession(Object o);

    ResponseEntity<ResultBean> getEntityList(QueryAllBean queryAllBean) throws Exception;
}


