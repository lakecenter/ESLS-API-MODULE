package com.wdy.module.dao;

import com.wdy.module.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RoleAndPermissionDao extends JpaRepository<RolePermission,Long> {
    RolePermission findByRoleIdAndPermissionId(Long roleId, Long permissionId);
    @Transactional
    Integer deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);
}
