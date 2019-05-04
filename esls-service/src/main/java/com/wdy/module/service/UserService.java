package com.wdy.module.service;


import com.wdy.module.dto.UserVo;
import com.wdy.module.entity.*;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService extends Service {
    List<User> findAll();

    List<User> findAll(Integer page, Integer count);

    User saveOne(User user);

    boolean deleteById(Long id);

    List<Permission> findPermissionByUserId(Long userId);

    List<Role> findRolesByUserId(Long userId);

    User findByName(String name);

    User findByTelephone(String telePhone);

    User findByMail(String mail);

    User findById(Long id);

    User registerUser(UserVo userVo) throws MessagingException;

    User registerUser(User user);

    void giveBasePermissionToUser(User user);
}
