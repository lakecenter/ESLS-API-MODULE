package com.wdy.module.service;


import com.wdy.module.entity.Logs;

import java.util.List;
import java.util.Optional;

public interface LogService extends Service {
    List<Logs> findAll();

    List<Logs> findAll(Integer page, Integer count);

    Logs saveOne(Logs shop);

    Optional<Logs> findById(Long id);

    boolean deleteById(Long id);
}
