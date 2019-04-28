package com.wdy.module.serviceImpl;

import com.wdy.module.dao.RoleDao;
import com.wdy.module.entity.Role;
import com.wdy.module.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("RoleService")
public class RoleServiceImpl extends BaseServiceImpl  implements RoleService {
    @Autowired
    private RoleDao roleDao;
    @Override
    public List<Role> findAll() {
        return roleDao.findAll();
    }

    @Override
    public List<Role> findAll(Integer page, Integer count) {
        List<Role> content = roleDao.findAll(PageRequest.of(page, count, Sort.Direction.DESC, "id")).getContent();
        return content;
    }

    @Override
    public Role saveOne(Role role) {
        return roleDao.save(role);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleDao.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        try{
            roleDao.deleteById(id);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
