package com.wdy.module.service;


import com.wdy.module.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService extends Service{
    List<Role> findAll();
    List<Role> findAll(Integer page, Integer count);
    Role saveOne(Role role);
    Optional<Role> findById(Long id);
    boolean deleteById(Long id);
}
