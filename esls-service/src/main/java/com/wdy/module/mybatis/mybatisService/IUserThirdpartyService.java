package com.wdy.module.mybatis.mybatisService;

import com.baomidou.mybatisplus.service.IService;
import com.wdy.module.entity.User;
import com.wdy.module.entity.UserThirdparty;
import com.wdy.module.mybatis.mybatisModel.ThirdPartyUser;

import java.util.List;

/**
 * <p>
 * 第三方用户表 服务类
 * </p>
 *
 * @author liugh123
 * @since 2018-07-27
 */
public interface IUserThirdpartyService extends IService<UserThirdparty> {
    List<UserThirdparty> findAll();
    UserThirdparty findById(Long id);
    Boolean saveOne(UserThirdparty userThirdparty);
    User insertThirdPartyUser(ThirdPartyUser param, String password)throws Exception;
}
