package com.wdy.module.serviceImpl;

import com.wdy.module.dao.UserAndRoleDao;
import com.wdy.module.entity.UserRole;
import com.wdy.module.service.UserAndRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("UserRoleService")
public class UserAndRoleServiceImpl extends BaseServiceImpl  implements UserAndRoleService {
    @Autowired
    private UserAndRoleDao userAndRoleDao;
    @Override
    public List<UserRole> findAll() {
        return userAndRoleDao.findAll();
    }

    @Override
    public List<UserRole> findAll(Integer page, Integer count) {
        List<UserRole> content = userAndRoleDao.findAll(PageRequest.of(page, count, Sort.Direction.DESC, "id")).getContent();
        return content;
    }

    @Override
    public List<UserRole> findByUserId(Long userId) {
        return userAndRoleDao.findByUserId(userId);
    }

    @Override
    @Transactional
    public UserRole saveOne(UserRole role) {
        return userAndRoleDao.save(role);
    }

    @Override
    public Optional<UserRole> findById(Long id) {
        return userAndRoleDao.findById(id);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        try{
            userAndRoleDao.deleteById(id);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
