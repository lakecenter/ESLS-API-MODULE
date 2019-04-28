package com.wdy.module.dao;

import com.wdy.module.entity.Good;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodDao extends JpaRepository<Good,Long> {
    Good findByBarCode(String BarCode);
}
