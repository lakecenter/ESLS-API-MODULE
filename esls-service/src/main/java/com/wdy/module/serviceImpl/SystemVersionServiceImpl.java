package com.wdy.module.serviceImpl;

import com.wdy.module.dao.SystemVersionDao;
import com.wdy.module.entity.SystemVersion;
import com.wdy.module.service.SystemVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("SystemVersionService")
public class SystemVersionServiceImpl extends BaseServiceImpl implements SystemVersionService {
    @Autowired
    private SystemVersionDao systemVersionDao;

    @Override
    public Optional<SystemVersion> findById(Long id) {
        return systemVersionDao.findById(id);
    }

    @Override
    public SystemVersion saveOne(SystemVersion systemVersion) {
        return systemVersionDao.save(systemVersion);
    }

    @Override
    public List<SystemVersion> findAll() {
        return systemVersionDao.findAll();
    }
}
