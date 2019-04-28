package com.wdy.module.dao;

import com.wdy.module.entity.Logs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogDao extends JpaRepository<Logs,Long> {
}
