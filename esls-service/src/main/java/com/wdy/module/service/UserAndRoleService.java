package com.wdy.module.service;


import com.wdy.module.entity.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserAndRoleService extends Service{
    List<UserRole> findAll();
    List<UserRole> findAll(Integer page, Integer count);
    List<UserRole> findByUserId(Long userId);
    UserRole saveOne(UserRole userRole);
    Optional<UserRole> findById(Long id);
    boolean deleteById(Long id);
}
