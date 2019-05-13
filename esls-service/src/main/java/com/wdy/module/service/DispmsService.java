package com.wdy.module.service;


import com.wdy.module.entity.Dispms;

import java.util.List;
import java.util.Optional;

public interface DispmsService extends Service {
    List<Dispms> findAll();

    List<Dispms> findAll(Integer page, Integer count);

    Dispms saveOne(Dispms dispms);

    Optional<Dispms> findById(Long id);

    boolean deleteById(Long id);

    Dispms findByStyleIdAndColumnTypeAndSourceColumn(Long styleId, String columnType, String sourceColumn);
}
