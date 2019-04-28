package com.wdy.module.dao;

import com.wdy.module.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceDao extends JpaRepository<Balance,Long> {
}
