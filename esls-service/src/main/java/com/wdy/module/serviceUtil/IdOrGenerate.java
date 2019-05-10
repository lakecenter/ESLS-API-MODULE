package com.wdy.module.serviceUtil;

import com.wdy.module.utils.ReflectUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.IdentityGenerator;

import java.io.Serializable;

/**
 * @program: esls-parent
 * @description: 自定义ID生成器
 * @author: dongyang_wu
 * @create: 2019-05-08 10:08
 */
public class IdOrGenerate extends IdentityGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object obj) throws HibernateException {
        if (obj == null) {
            throw new HibernateException(new NullPointerException());
        }
        String id1 = ReflectUtil.getSourceData("id", obj);
        if (StringUtil.isEmpty(id1))
            return super.generate(sharedSessionContractImplementor, obj);
        else
            return Long.valueOf(id1);
    }
}