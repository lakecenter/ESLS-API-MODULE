
package com.wdy.module.serviceImpl;

import com.wdy.module.common.request.RequestBean;
import com.wdy.module.service.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import com.wdy.module.service.Service;

import java.lang.reflect.ParameterizedType;
import java.util.List;

@org.springframework.stereotype.Service("BaseService")
public class BaseServiceImpl<T> implements Service<T> {
    @Autowired
    protected BaseDao baseDao;
    @SuppressWarnings("unchecked")
    private Class<T> getEntityClass() {
        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return entityClass;
    }
    @Override
    public List findBySql(String s) {
        return baseDao.findBySql(s);
    }

    @Override
    public List findBySql(String s,Class clazz) {
        return baseDao.findBySql(s,clazz);
    }

    @Override
    public List findAllBySql(String table, String query, String queryString, int page, int count,Class clazz) {
        return baseDao.findAllBySql(table,query,queryString,page,count,clazz);
    }

    @Override
    public  List findByArrtribute(String table, String query, String queryString,Class clazz) {
        return baseDao.findByArrtribute(table,query,queryString,clazz);
    }

    @Override
    public List findAllBySql(String table, String query, String queryString,Class clazz) {
        return baseDao.findAllBySql(table,query, queryString,clazz);
    }

    @Override
    public Integer updateByArrtribute(String table, RequestBean source, RequestBean target) {
        return baseDao.updateByArrtribute(table,source,target);
    }

    @Override
    public List findAllBySql(String table, String connection, RequestBean requestBean, int page, int count, Class clazz) {
        return  baseDao.findAllBySql(table,connection,requestBean,page,count,clazz);
    }

    @Override
    public List findAllBySql(String table, String connection, String query, String queryString, int page, int count, Class clazz) {
        return baseDao.findAllBySql(table,connection,query,queryString,page,count,clazz);
    }

    @Override
    public void save(T bean) {
        baseDao.save(bean);
    }

    @Override
    public void update(T bean) {
        baseDao.update(bean);
    }

    @Override
    public T find(Long id) {
        return (T) baseDao.findById(id,getEntityClass());
    }

    @Override
    public int delete(Long id) {
        return baseDao.deleteById(id,getEntityClass());
    }

    @Override
    public List<T> findAlls() {
        return baseDao.findAll(getEntityClass());
    }

    @Override
    public List<T> findAlls(Integer page, Integer pageSize) {
        return baseDao.findAll(page,pageSize,getEntityClass());
    }

    @Override
    public Integer deleteByIdList(String table, String query, List idList) {
        return baseDao.deleteByIdList(table,query,idList);
    }
}
