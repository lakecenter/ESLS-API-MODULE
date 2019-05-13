package com.wdy.module.service;


import com.wdy.module.entity.CycleJob;

import java.util.List;

public interface CycleJobService extends Service {
    List<CycleJob> findAll();

    List<CycleJob> findAll(Integer page, Integer count);

    CycleJob findByMode(Integer mode);

    CycleJob saveOne(CycleJob cycleJob);

    CycleJob findById(Long id);

    boolean deleteById(Long id);
}
