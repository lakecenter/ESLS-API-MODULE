package com.wdy.module.serviceImpl;

import com.wdy.module.dao.PermissionDao;
import com.wdy.module.entity.Permission;
import com.wdy.module.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("PermissionService")
public class PermissionServiceImpl extends BaseServiceImpl implements PermissionService {
    @Autowired
    private PermissionDao permissionDao;

    @Override
    public List<Permission> findAll() {
        return permissionDao.findAll();
    }
    @Override
    public List<Permission> findAll(Integer page, Integer count) {
        List<Permission> content = permissionDao.findAll(PageRequest.of(page, count, Sort.Direction.DESC, "id")).getContent();
        return content;
    }
    @Override
    public Permission saveOne(Permission permission) {
        return permissionDao.save(permission);
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return permissionDao.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        try{
            permissionDao.deleteById(id);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
