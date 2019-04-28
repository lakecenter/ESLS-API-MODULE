package com.wdy.module.mybatis.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.wdy.module.entity.UserThirdparty;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 第三方用户表 Mapper 接口
 * </p>
 *
 * @author liugh123
 * @since 2018-07-27
 */
public interface UserThirdpartyMapper extends BaseMapper<UserThirdparty> {
    @Select("select * from user_thirdparty")
    List<UserThirdparty> findAll();
}
