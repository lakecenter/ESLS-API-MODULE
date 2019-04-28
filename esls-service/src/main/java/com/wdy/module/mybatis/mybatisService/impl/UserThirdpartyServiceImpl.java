//package com.wdy.module.mybatis.mybatisService.impl;
//
//import com.baomidou.mybatisplus.service.impl.ServiceImpl;
//import com.wdy.module.common.constant.Constant;
//import com.wdy.module.entity.User;
//import com.wdy.module.mybatis.mapper.UserThirdpartyMapper;
//import com.wdy.module.entity.UserThirdparty;
//import com.wdy.module.mybatis.mybatisModel.ThirdPartyUser;
//import com.wdy.module.mybatis.mybatisService.IUserThirdpartyService;
//import com.wdy.module.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
///**
// * <p>
// * 第三方用户表 服务实现类
// * </p>
// *
// * @author liugh123
// * @since 2018-07-27
// */
//@Service
//public class UserThirdpartyServiceImpl extends ServiceImpl<UserThirdpartyMapper, UserThirdparty> implements IUserThirdpartyService {
//
//    @Autowired
//    private UserService userService;
//
//    @Override
//    public User insertThirdPartyUser(ThirdPartyUser param, String password) throws Exception{
//        User sysUser = User.builder().passwd(password).("游客"+param.getOpenid()).mobile(param.getOpenid())
//                .avatar(param.getAvatarUrl()).build();
//        User register = userService.register(sysUser, Constant.RoleType.USER);
//        // 初始化第三方信息
//        UserThirdparty thirdparty = UserThirdparty.builder().providerType(param.getProvider()).openId(param.getOpenid()).createTime(System.currentTimeMillis())
//                .userNo(register.getUserNo()).status(Constant.ENABLE).accessToken(param.getToken()).build();
//        this.insert(thirdparty);
//        return register;
//    }
//}
