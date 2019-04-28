package com.wdy.module.dao;

import com.wdy.module.entity.CycleJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CycleJobDao extends JpaRepository<CycleJob,Long> {
    CycleJob findByMode(Integer mode);
}
