package com.wdy.module.mybatis.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.wdy.module.entity.OperationLog;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 操作日志表 Mapper 接口
 * </p>
 *
 * @author wdy
 * @since 2019-04-26
 */
public interface OperationLogMapper extends BaseMapper<OperationLog> {
    @Select("select * from operation_log order by createTime desc")
    List<OperationLog> findAll();
}
