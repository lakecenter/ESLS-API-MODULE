package com.wdy.module.serviceImpl;

import com.wdy.module.dao.RoleAndPermissionDao;
import com.wdy.module.entity.RolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.wdy.module.service.RolePermissionService;

import java.util.List;
import java.util.Optional;


@Service("RolePermissionService")
public class RolePermissionServiceImpl extends BaseServiceImpl implements RolePermissionService {
    @Autowired
    private RoleAndPermissionDao roleAndPermissionDao;

    @Override
    public List<RolePermission> findAll() {
        return roleAndPermissionDao.findAll();
    }
    @Override
    public List<RolePermission> findAll(Integer page, Integer count) {
        List<RolePermission> content = roleAndPermissionDao.findAll(PageRequest.of(page, count, Sort.Direction.DESC, "id")).getContent();
        return content;
    }
    @Override
    public RolePermission saveOne(RolePermission permission) {
        return roleAndPermissionDao.save(permission);
    }

    @Override
    public Optional<RolePermission> findById(Long id) {
        return roleAndPermissionDao.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        try{
            roleAndPermissionDao.deleteById(id);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
