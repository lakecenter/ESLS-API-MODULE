
package com.wdy.module.serviceImpl;

import com.wdy.module.common.constant.TableConstant;
import com.wdy.module.common.exception.ResultEnum;
import com.wdy.module.common.exception.ServiceException;
import com.wdy.module.common.request.QueryAllBean;
import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.response.ResponseHelper;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.service.BaseDao;
import com.wdy.module.serviceUtil.CopyUtil;
import com.wdy.module.serviceUtil.SpringContextUtil;
import com.wdy.module.utils.ConditionUtil;
import com.wdy.module.utils.ReflectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.wdy.module.service.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
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
    public List findBySql(String s, Class clazz) {
        return baseDao.findBySql(s, clazz);
    }

    @Override
    public List findAllBySql(String table, String query, String queryString, Integer page, Integer count, Class clazz) {
        return baseDao.findAllBySql(table, query, queryString, page, count, clazz);
    }

    @Override
    public List findByArrtribute(String table, String query, String queryString, Class clazz) {
        return baseDao.findByArrtribute(table, query, queryString, clazz);
    }

    @Override
    public List findAllBySql(String table, String query, String queryString, Class clazz) {
        return baseDao.findAllBySql(table, query, queryString, clazz);
    }

    @Override
    @Transactional
    public Integer updateByArrtribute(String table, RequestBean source, RequestBean target) {
        return baseDao.updateByArrtribute(table, source, target);
    }

    @Override
    public List findAllBySql(String table, String connection, RequestBean requestBean, Integer page, Integer count, Class clazz) {
        return baseDao.findAllBySql(table, connection, requestBean, page, count, clazz);
    }

    @Override
    public List findAllBySql(String table, String connection, String query, String queryString, Integer page, Integer count, Class clazz) {
        return baseDao.findAllBySql(table, connection, query, queryString, page, count, clazz);
    }

    @Override
    @Transactional
    public void save(T bean) {
        baseDao.save(bean);
    }

    @Override
    @Transactional
    public void update(T bean) {
        baseDao.update(bean);
    }

    @Override
    public T find(Long id) {
        return (T) baseDao.findById(id, getEntityClass());
    }

    @Override
    public int delete(Long id) {
        return baseDao.deleteById(id, getEntityClass());
    }

    @Override
    public List<T> findAlls() {
        return baseDao.findAll(getEntityClass());
    }

    @Override
    public List<T> findAlls(Integer page, Integer pageSize) {
        return baseDao.findAll(page, pageSize, getEntityClass());
    }

    @Override
    @Transactional
    public void refreshSession(Object o) {
        baseDao.refreshSession(o);
    }

    @Override
    public ResponseEntity<ResultBean> getEntityList(QueryAllBean queryAllBean) throws Exception {
        String result = queryAllBean.getResult(), serviceName = queryAllBean.getServiceName();
        if (result == null)
            throw new ServiceException(ResultEnum.QUERY_LIST_ARGS_ERROR);
        Object service = SpringContextUtil.getBean(serviceName);
        String entityName = serviceName.substring(0, serviceName.indexOf("Service"));
        String tableName = TableConstant.entityNameToTableName.get(entityName);
        Class clazz = Class.forName("com.wdy.module.entity." + entityName);
        List resultList = null;
        List list = null;
        String query = queryAllBean.getQuery(), queryString = queryAllBean.getQueryString();
        Integer page = queryAllBean.getPage(), count = queryAllBean.getPagecount();
        // 带条件或查询
        if (query != null && query.contains(" ")) {
            Service baseService = (Service) SpringContextUtil.getBean("BaseService");
            List content = baseService.findAllBySql(tableName, "like", query, queryString, page, count, clazz);
            resultList = CopyUtil.copyEntity(entityName, content);
        }
        // 查询全部
        if (result.equals(ConditionUtil.QUERY_ALL)) {
            Method findAll = ReflectUtil.reflectClassMethod(service, "findAll");
            list = (List) findAll.invoke(service);
            resultList = CopyUtil.copyEntity(entityName, list);
        }
        // 查询全部分页
        if (result.equals(ConditionUtil.QUERY_ALL_PAGE)) {
            Method findAll = ReflectUtil.reflectClassMethod(service, "findAll");
            list = (List) findAll.invoke(service);
            Method findAllByPage = ReflectUtil.reflectClassMethod(service, "findAll", Integer.class, Integer.class);
            List content = (List) findAllByPage.invoke(service, page, count);
            resultList = CopyUtil.copyEntity(entityName, content);
        }
        // 带条件查询全部
        if (result.equals(ConditionUtil.QUERY_ATTRIBUTE_ALL)) {
            Service baseService = (Service) SpringContextUtil.getBean("BaseService");
            List content = baseService.findAllBySql(tableName, query, queryString, clazz);
            resultList = CopyUtil.copyEntity(entityName, content);
        }
        // 带条件查询分页
        if (result.equals(ConditionUtil.QUERY_ATTRIBUTE_PAGE)) {
            Method findAll = ReflectUtil.reflectClassMethod(service, "findAll");
            list = (List) findAll.invoke(service);
            Service baseService = (Service) SpringContextUtil.getBean("BaseService");
            List content = baseService.findAllBySql(tableName, query, queryString, page, count, clazz);
            resultList = CopyUtil.copyEntity(entityName, content);
        }
        if (resultList == null)
            return ResponseHelper.BadRequest("查询组合出错 函数未执行！");
        return ResponseHelper.OK(resultList, list != null ? list.size() : resultList.size());
    }

    @Override
    public Integer deleteByIdList(String table, String query, List idList) {
        return baseDao.deleteByIdList(table, query, idList);
    }
}
