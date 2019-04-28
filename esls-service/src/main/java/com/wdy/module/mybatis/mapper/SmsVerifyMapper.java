package com.wdy.module.mybatis.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.wdy.module.entity.SmsVerify;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 验证码发送记录 Mapper 接口
 * </p>
 *
 * @author wdy
 * @since 2019-04-26
 */
public interface SmsVerifyMapper extends BaseMapper<SmsVerify> {
    @Select("select * from sms_verify")
    List<SmsVerify> findAll();
}
