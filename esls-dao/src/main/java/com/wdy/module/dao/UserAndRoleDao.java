package com.wdy.module.dao;

import com.wdy.module.entity.UserRole;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserAndRoleDao extends JpaRepository<UserRole,Long> {
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO user_role(roleId,userId) SELECT :roleId , :userId FROM DUAL WHERE NOT EXISTS(SELECT * FROM user_role b WHERE b.roleId=:roleId AND b.userId=:userId)",nativeQuery = true)
    Integer insertByCondition(@Param("roleId") Long roleId, @Param("userId") Long userId);
    @Transactional
    @Modifying
    Integer deleteByUserIdAndRoleId(Long userId, Long roleId);
    UserRole findByUserIdAndRoleId(Long userId, Long roleId);
    List<UserRole> findByUserId(Long userId);
}
