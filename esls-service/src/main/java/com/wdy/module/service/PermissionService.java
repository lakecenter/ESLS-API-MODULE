package com.wdy.module.service;


import com.wdy.module.entity.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionService extends Service{
    List<Permission> findAll();
    List<Permission> findAll(Integer page, Integer count);
    Permission saveOne(Permission permission);
    Optional<Permission> findById(Long id);
    boolean deleteById(Long id);
}
