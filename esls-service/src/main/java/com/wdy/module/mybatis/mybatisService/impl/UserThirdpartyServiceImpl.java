package com.wdy.module.mybatis.mybatisService.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wdy.module.common.constant.Constant;
import com.wdy.module.entity.User;
import com.wdy.module.mybatis.mapper.UserThirdpartyMapper;
import com.wdy.module.entity.UserThirdparty;
import com.wdy.module.mybatis.mybatisModel.ThirdPartyUser;
import com.wdy.module.mybatis.mybatisService.IUserThirdpartyService;
import com.wdy.module.service.UserService;
import com.wdy.module.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 第三方用户表 服务实现类
 * </p>
 *
 * @author liugh123
 * @since 2018-07-27
 */
@Service
public class UserThirdpartyServiceImpl extends ServiceImpl<UserThirdpartyMapper, UserThirdparty> implements IUserThirdpartyService {

    @Autowired
    private UserService userService;

    @Override
    public List<UserThirdparty> findAll() {
        return this.baseMapper.findAll();
    }

    @Override
    public UserThirdparty findById(Long id) {
        return this.selectById(id);
    }

    @Override
    public Boolean saveOne(UserThirdparty userThirdparty) {
        return this.insertOrUpdate(userThirdparty);
    }

    @Override
    public User insertThirdPartyUser(ThirdPartyUser param, String password) {
        String userName = "游客" + param.getOpenid();
        User sysUser = User.builder().passwd(MD5Util.md5UserPassword(password, userName)).name(userName).telephone(param.getOpenid())
                .avatarUrl(param.getAvatarUrl()).rawPasswd(password).build();
        User register = userService.registerUser(sysUser);
        // 初始化第三方信息
        UserThirdparty thirdParty = UserThirdparty.builder().providerType(param.getProvider()).openId(param.getOpenid()).createTime(System.currentTimeMillis())
                .userName(register.getName()).status(Constant.ENABLE).accessToken(param.getToken()).build();
        this.insert(thirdParty);
        return register;
    }
}
