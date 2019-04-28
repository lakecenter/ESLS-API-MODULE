package com.wdy.module.dao;

import com.wdy.module.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserDao extends JpaRepository<User,Long> {
    @Query(value = "SELECT t.* FROM user_role AS u,role_permission AS r ,permission t WHERE u.user_id =?1 AND u.role_id = r.role_id AND r.permission_id = t.id",nativeQuery = true)
    List<Permission> findPermissionByUserId(Long userId);
    @Query(value = "SELECT p.* FROM permission AS p INNER JOIN role_permission AS rp ON p.id = rp.permission_id INNER JOIN user_role AS ur ON ur.role_id = rp.role_id WHERE ur.user_id = ?1",nativeQuery = true)
    List<Role> findRolesByUserId(Long userId);
    User findByName(String name);
    User findByTelephone(String telePhone);
    User findByMail(String mail);
}
