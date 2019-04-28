package com.wdy.module.dao;

import com.wdy.module.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopDao extends JpaRepository<Shop,Long> {
}
