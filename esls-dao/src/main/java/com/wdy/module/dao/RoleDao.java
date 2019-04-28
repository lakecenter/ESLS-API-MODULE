package com.wdy.module.dao;

import com.wdy.module.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDao  extends JpaRepository<Role,Long> {
    Role findByType(String type);
}
