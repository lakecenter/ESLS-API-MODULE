package com.wdy.module.dao;

import com.wdy.module.entity.SystemVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemVersionDao extends JpaRepository<SystemVersion,Long> {
}
