package com.wdy.module.dynamicquery;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 动态jpql/nativesql查询的实现类
 */
@Repository
@Slf4j
public class DynamicQueryImpl implements DynamicQuery {

    @PersistenceContext
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * 从EntityManager获取Hibernate的Session 下面所有方式的实现都基于Hibernate
     */
    private Session getHibernateSession() {
        return em.unwrap(Session.class);
    }

    /**
     * 获取Hibernate的SessionFactory对象
     *
     * @return
     */
    private SessionFactory getHibernateSessionFactory() {
        return getHibernateSession().getSessionFactory();
    }

    @Override
    public void save(Object entity) {
        em.persist(entity);
    }

    @Override
    public void update(Object entity) {
        em.merge(entity);
    }

    @Override
    public <T> void delete(Class<T> entityClass, Object entityid) {
        delete(entityClass, new Object[]{entityid});
    }

    @Override
    public <T> void delete(Class<T> entityClass, Object[] entityids) {
        for (Object id : entityids) {
            em.remove(em.getReference(entityClass, id));
        }
    }

    @Override
    public <T> T querySingleResult(Class<T> resultClass, String jpql, @SuppressWarnings("rawtypes") Map paramsMap) {
        return createTypedQuery(resultClass, jpql, paramsMap).getSingleResult();
    }

    @Override
    public <T> List<T> query(Class<T> resultClass, String jpql, @SuppressWarnings("rawtypes") Map paramsMap) {
        return createTypedQuery(resultClass, jpql, paramsMap).getResultList();
    }

    @Override
    public Long queryCount(String jpql, Object... params) {
        jpql = StringUtils.substringBefore(jpql, "order by"); // 去掉order by,
        // 提升执行效率
        // 去重和分组只能使用NativeSQL统计查询
        if (jpql.contains("distinct") || jpql.contains("group by")) {
            String countSql = generateCountSql(jpql);
            Object count = createNativeQuery(countSql, params).getSingleResult();
            return ((Number) count).longValue();
        } else { // 使用jpql统计查询
            String countJpql = generateCountJpql(jpql);
            return (Long) createQuery(countJpql, params).getSingleResult();
        }
    }

    @Override
    public int executeUpdate(String jpql, Object... params) {
        return createQuery(jpql, params).executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T nativeQuerySingleResult(Class<T> resultClass, String nativeSql, Object... params) {
        Query q = createNativeQuery(resultClass, nativeSql, params);
        List<T> list = q.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T nativeQuerySingleResult(String nativeSql, Object... params) {
        Query q = createNativeQuery(null, nativeSql, params);
        List<T> list = q.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> query(String nativeSql, Object... params) {
        Query q = createNativeQuery(null, nativeSql, params);
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> query(Class<T> resultClass, String nativeSql, Object... params) {
        Query q = createNativeQuery(resultClass, nativeSql, params);
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> nativeQueryPagingList(Class<T> resultClass, Pageable pageable, String nativeSql,
                                             Object... params) {
        Integer pageNumber = pageable.getPageNumber();
        Integer pageSize = pageable.getPageSize();
        Integer startPosition = pageNumber * pageSize;
        return createNativeQuery(resultClass, nativeSql, params).setFirstResult(startPosition).setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public <T> Page<T> nativeQuery(Class<T> resultClass, Pageable pageable, String nativeSql, Object... params) {
        List<T> rows = nativeQueryPagingList(resultClass, pageable, nativeSql, params);
        Long total = nativeQueryCount(nativeSql, params);
        return new PageImpl<T>(rows, pageable, total);
    }

    @Override
    public Long nativeQueryCount(String nativeSql, Object... params) {
        nativeSql = StringUtils.substringBefore(nativeSql, "order by"); // 去掉order
        // by,
        // 提升执行效率
        // String countSql = "select count(*) from (" + nativeSql + ") _count";
        Object count = createNativeQuery(nativeSql, params).getSingleResult();
        return ((Number) count).longValue();
    }

    @Override
    public int nativeExecuteUpdate(String nativeSql, Object... params) {
        return createNativeQuery(nativeSql, params).executeUpdate();
    }

    private Query createQuery(String jpql, Object... params) {
        Query q = em.createQuery(jpql);
        for (int i = 0; i < params.length; i++) {
            q.setParameter(i + 1, params[i]); // 与Hiberante不同,jpa query从位置1开始
        }
        return q;
    }

    @SuppressWarnings("unchecked")
    private <T> TypedQuery<T> createTypedQuery(Class<T> resultClass, String jpql,
                                               @SuppressWarnings("rawtypes") Map paramsMap) {
        TypedQuery<T> q = em.createNamedQuery(jpql, resultClass);
        Iterator<String> it = paramsMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            q.setParameter(key, paramsMap.get(key));
        }
        return q;
    }

    private Query createNativeQuery(String sql, Object... params) {
        Query q = em.createNativeQuery(sql);
        for (int i = 0; i < params.length; i++) {
            q.setParameter(i + 1, params[i]); // 与Hiberante不同,jpa query从位置1开始
        }
        return q;
    }

    private <T> Query createNativeQuery(Class<T> resultClass, String sql, Object... params) {
        Query q = null;
        if (resultClass == null) {
            q = em.createNativeQuery(sql);
        } else {
            q = em.createNativeQuery(sql, resultClass);
        }
        for (int i = 0; i < params.length; i++) {
            q.setParameter(i + 1, params[i]); // 与Hiberante不同,jpa query从位置1开始
        }
        return q;
    }

    /**
     * 执行统计查询
     *
     * @param jpql
     * @param params 命名参数
     * @return
     */
    private String generateCountJpql(String jpql) {
        return "select count(*) from " + StringUtils.substringAfter(jpql, "from");
    }

    /**
     * 通过jpql生成统计sql
     *
     * @param jpql
     * @return
     */
    private String generateCountSql(String jpql) {
        return "select count(*) c from (" + jpqlToSql(jpql) + ") _count";
    }

    /**
     * 通过hibernate的翻译器(QueryTranslator)将jpql翻译成sql
     *
     * @param jpql
     * @return
     */
    private String jpqlToSql(String jpql) {
        QueryTranslator qt = new QueryTranslatorImpl(jpql, jpql, Collections.EMPTY_MAP,
                (SessionFactoryImplementor) getHibernateSessionFactory());
        qt.compile(Collections.EMPTY_MAP, false);
        return qt.getSQLString();
    }

    public <T> List<T> directQuery(Class<T> resultClass, String jpql, Map<String, Object> paramsMap) {
        return getDirectQuery(resultClass, jpql, paramsMap).getResultList();
    }

    private <T> TypedQuery<T> getDirectQuery(Class<T> resultClass, String jpql, Map<String, Object> paramsMap) {
        TypedQuery<T> q = em.createQuery(jpql, resultClass);

        for (Iterator<String> it = paramsMap.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            q.setParameter(key, paramsMap.get(key));
        }
        return q;
    }

    @Override
    public <T> T directQuerySingleResult(Class<T> resultClass, String jpql, Map<String, Object> paramsMap) {
        try {
            return getDirectQuery(resultClass, jpql, paramsMap).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public <T> List<T> directQueryPagingList(Class<T> resultClass, Pageable pageable, String nativeSql,
                                             Map<String, Object> paramsMap) {
        Integer pageNumber = pageable.getPageNumber();
        Integer pageSize = pageable.getPageSize();
        Integer startPosition = pageNumber * pageSize;
        return getDirectQuery(resultClass, nativeSql, paramsMap).setFirstResult(startPosition).setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public <T> TypedQuery<T> createTypedQueryByJpqlString(String qlString, Class<T> resultClass) {
        return this.em.createQuery(qlString, resultClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T nativeQueryModel(Class<T> resultClass, String nativeSql, Object... params) {
        Query q = createNativeQuery(nativeSql, params);
        q.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(resultClass));
        List<T> list = q.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}
