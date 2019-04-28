package com.wdy.module.mybatis.mybatisService.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wdy.module.mybatis.mapper.OperationLogMapper;
import com.wdy.module.entity.OperationLog;
import com.wdy.module.mybatis.mybatisService.IOperationLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 操作日志表 服务实现类
 * </p>
 *
 * @author wdy
 * @since 2019-04-26
 */
@Service("OperationLogService")
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements IOperationLogService {

    @Override
    public List<OperationLog> findAll() {
        return this.baseMapper.findAll();
    }

    @Override
    public OperationLog findById(Long id) {
        return this.selectById(id);
    }

    @Override
    public Boolean saveOne(OperationLog operationLog) {
        return this.insertOrUpdate(operationLog);
    }
}
