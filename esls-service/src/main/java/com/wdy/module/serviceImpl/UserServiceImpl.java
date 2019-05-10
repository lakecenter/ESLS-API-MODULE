package com.wdy.module.serviceImpl;

import com.wdy.module.common.exception.ResultEnum;
import com.wdy.module.dao.RoleDao;
import com.wdy.module.dao.UserAndRoleDao;
import com.wdy.module.dao.UserDao;
import com.wdy.module.dto.UserVo;
import com.wdy.module.entity.*;
import com.wdy.module.common.exception.ServiceException;
import com.wdy.module.service.UserService;
import com.wdy.module.system.SystemVersionArgs;
import com.wdy.module.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.util.*;

@Service("UserService")
public class UserServiceImpl extends BaseServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserAndRoleDao userAndRoleDao;

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public List<User> findAll(Integer page, Integer count) {
        List<User> content = userDao.findAll(PageRequest.of(page, count, Sort.Direction.DESC, "id")).getContent();
        return content;
    }

    @Override
    @Transactional
    public User saveOne(User user) {
        return userDao.save(user);
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            userDao.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Permission> findPermissionByUserId(Long userId) {
        return userDao.findPermissionByUserId(userId);
    }

    @Override
    public List<Role> findRolesByUserId(Long userId) {
        return userDao.findRolesByUserId(userId);
    }

    @Override
    public User findByName(String name) {
        return userDao.findByName(name);
    }

    @Override
    public User findByTelephone(String telePhone) {
        return userDao.findByTelephone(telePhone);
    }

    @Override
    public User findByMail(String mail) {
        return userDao.findByMail(mail);
    }

    @Override
    public User findById(Long id) {
        Optional<User> user = userDao.findById(id);
        if (user.isPresent())
            return userDao.findById(id).get();
        else
            return null;
    }

    @Override
    public User registerUser(UserVo userVo) throws MessagingException {
        User u = findByName(userVo.getName());
        User byTelephone = findByTelephone(userVo.getTelephone());
        User byMail = findByMail(userVo.getMail());
        if (u != null || byTelephone != null || byMail != null)
            throw new ServiceException(ResultEnum.USER_EXIST);
        if (!RegexUtil.checkEmail(userVo.getMail()))
            throw new ServiceException(ResultEnum.USER_MAIL_ERROR);
        if (!RegexUtil.checkPhoneNumber(userVo.getTelephone()))
            throw new ServiceException(ResultEnum.USER_PHONE_ERROR);
        if (StringUtils.equals(userVo.getMail(), userVo.getName()))
            throw new ServiceException(ResultEnum.USER_USERNAME_EMAIL_ERROR);
        if (StringUtils.equals(userVo.getTelephone(), userVo.getName()))
            throw new ServiceException(ResultEnum.USER_USERNAME_PHONE_ERROR);
        else {
            User user = new User();
            BeanUtils.copyProperties(userVo, user);
            user.setPasswd(MD5Util.md5UserPassword(userVo.getPasswd(), userVo.getName()));
            user.setStatus((byte) 1);
            user.setActivateStatus((byte) 0);
            user.setCreateTime(new Timestamp(System.currentTimeMillis()));
            String code = "ACTIVATEUSER_" + UUID.randomUUID();
            redisUtil.sentinelSet(code, user, (long) (60000 * 5));
            String content = "<html><head></head><body><h1>亲爱的用户" + user.getName() + "，这是一封激活邮件,请5分钟内进行激活，否则邮件失效，点击以下链接即可激活</h1><h3><a href='http://" + SystemVersionArgs.outNetIp + ":8086/user/activate?code="
                    + code + "'>http://" + SystemVersionArgs.outNetIp + ":8086/user/activate?code=" + code
                    + "</href></h3></body></html>";
            try {
                mailSender.sendSSLMail(user.getMail(), "ESLS系统激活邮件", content);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return user;
        }
    }

    @Override
    public User registerUser(User user) {
        user.setCreateTime(new Timestamp(System.currentTimeMillis()));
        User result = saveOne(user);
        if (result != null) {
            this.giveBasePermissionToUser(result);
            return result;
        }
        return null;
    }

    @Override
    public void giveBasePermissionToUser(User user) {
        Role role = roleDao.findByType("基础权限");
        if (user != null && role != null) {
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(role.getId());
            if (userAndRoleDao.findByUserIdAndRoleId(user.getId(), role.getId()) == null)
                userAndRoleDao.save(userRole);
        }
    }

}
