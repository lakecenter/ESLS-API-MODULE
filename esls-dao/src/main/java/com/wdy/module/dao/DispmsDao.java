package com.wdy.module.dao;

import com.wdy.module.entity.Dispms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DispmsDao extends JpaRepository<Dispms,Long> {
    List<Dispms> findByStyleId(Long StyleId);
    Dispms findByStyleIdAndColumnTypeAndSourceColumn(Long styleId, String columnType, String sourceColumn);
}
