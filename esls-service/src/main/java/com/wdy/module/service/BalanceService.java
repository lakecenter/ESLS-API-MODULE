package com.wdy.module.service;


import com.wdy.module.entity.Balance;

import java.util.List;

public interface BalanceService<T> extends Service<T>{
    List<Balance> findAll();
    List<Balance> findAll(Integer page, Integer count);
    Balance saveOne(Balance balance);
    Balance findById(Long id);
    boolean deleteById(Long id);
}
