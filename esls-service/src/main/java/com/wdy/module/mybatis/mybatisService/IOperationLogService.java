package com.wdy.module.mybatis.mybatisService;

import com.baomidou.mybatisplus.service.IService;
import com.wdy.module.entity.OperationLog;

import java.util.List;

/**
 * <p>
 * 操作日志 服务类
 * </p>
 *
 * @author liugh123
 * @since 2018-05-08
 */
public interface IOperationLogService extends IService<OperationLog> {
    List<OperationLog> findAll();
    OperationLog findById(Long id);
    Boolean saveOne(OperationLog operationLog);
}
