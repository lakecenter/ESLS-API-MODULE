package com.wdy.module.service;


import com.wdy.module.entity.RolePermission;

import java.util.List;
import java.util.Optional;

public interface RolePermissionService {
    List<RolePermission> findAll();
    List<RolePermission> findAll(Integer page, Integer count);
    RolePermission saveOne(RolePermission rolePermission);
    Optional<RolePermission> findById(Long id);
    boolean deleteById(Long id);
}
