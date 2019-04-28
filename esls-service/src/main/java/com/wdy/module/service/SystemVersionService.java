package com.wdy.module.service;


import com.wdy.module.entity.SystemVersion;

import java.util.List;
import java.util.Optional;

public interface SystemVersionService extends Service {
    // 获取指定ID的标签
    Optional<SystemVersion> findById(Long id);

    SystemVersion saveOne(SystemVersion systemVersion);

    List<SystemVersion> findAll();
}
