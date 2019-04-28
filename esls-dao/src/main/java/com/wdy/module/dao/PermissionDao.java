package com.wdy.module.dao;

import com.wdy.module.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionDao extends JpaRepository<Permission,Long> {
    Permission findByName(String name);
}
