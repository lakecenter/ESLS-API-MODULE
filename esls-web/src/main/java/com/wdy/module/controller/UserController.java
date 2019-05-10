package com.wdy.module.controller;

import com.wdy.module.aop.CurrentUser;
import com.wdy.module.common.constant.TableConstant;
import com.wdy.module.common.exception.ResultEnum;
import com.wdy.module.common.request.*;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.dao.RoleDao;
import com.wdy.module.dao.UserAndRoleDao;
import com.wdy.module.dto.UserVo;
import com.wdy.module.entity.*;
import com.wdy.module.aop.Log;
import com.wdy.module.common.exception.ServiceException;
import com.wdy.module.service.RoleService;
import com.wdy.module.service.UserService;
import com.wdy.module.serviceUtil.*;
import com.wdy.module.system.SystemVersionArgs;
import com.wdy.module.utils.*;
import io.swagger.annotations.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.*;

@RestController
@Api(description = "用户管理API")
@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserAndRoleDao userAndRoleDao;
    @Autowired
    private RoleService roleService;

    @ApiOperation(value = "根据条件获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件 可为所有字段 分隔符为单个空格 ", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryString", value = "查询条件的字符串", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "数量", dataType = "int", paramType = "query")
    })
    @GetMapping("/users")
    @RequiresPermissions("系统菜单")
    public ResponseEntity<ResultBean> getGoods(@RequestParam(required = false) String query, @RequestParam(required = false) String queryString, @Min(message = "data.page.min", value = 0) @RequestParam(required = false) Integer page, @RequestParam(required = false) @Min(message = "data.count.min", value = 0) Integer count) {
        String result = ConditionUtil.judgeArgument(query, queryString, page, count);
        if (result == null)
            return new ResponseEntity<>(ResultBean.error("参数组合有误 [query和queryString必须同时提供] [page和count必须同时提供]"), HttpStatus.BAD_REQUEST);
        // 带条件或查询
        if (query != null && query.contains(" ")) {
            List content = userService.findAllBySql(TableConstant.TABLE_USER, "like", query, queryString, page, count, User.class);
            return new ResponseEntity<>(new ResultBean(content, content.size()), HttpStatus.OK);
        }
        // 查询全部
        if (result.equals(ConditionUtil.QUERY_ALL)) {
            List list = userService.findAll();
            List content = CopyUtil.copyUser(list);
            return new ResponseEntity<>(new ResultBean(content, list.size()), HttpStatus.OK);
        }
        // 查询全部分页
        if (result.equals(ConditionUtil.QUERY_ALL_PAGE)) {
            List list = userService.findAll();
            List content = userService.findAll(page, count);
            return new ResponseEntity<>(new ResultBean(CopyUtil.copyUser(content), list.size()), HttpStatus.OK);
        }
        // 带条件查询全部
        if (result.equals(ConditionUtil.QUERY_ATTRIBUTE_ALL)) {
            List content = userService.findAllBySql(TableConstant.TABLE_USER, query, queryString, User.class);
            return new ResponseEntity<>(new ResultBean(CopyUtil.copyUser(content), content.size()), HttpStatus.OK);
        }
        // 带条件查询分页
        if (result.equals(ConditionUtil.QUERY_ATTRIBUTE_PAGE)) {
            List list = userService.findAll();
            List content = userService.findAllBySql(TableConstant.TABLE_USER, query, queryString, page, count, User.class);
            return new ResponseEntity<>(new ResultBean(CopyUtil.copyUser(content), list.size()), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResultBean.error("查询组合出错 函数未执行！"), HttpStatus.OK);
    }

    @ApiOperation(value = "获取指定ID的用户信息")
    @GetMapping("/users/{id}")
    @Transactional
    @RequiresPermissions("获取指定ID的信息")
    public ResponseEntity<ResultBean> getGoodById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null)
            return new ResponseEntity<>(ResultBean.error("此ID用户不存在"), HttpStatus.BAD_REQUEST);
        List users = new ArrayList<>();
        users.add(user);
        return new ResponseEntity<>(new ResultBean(users), HttpStatus.OK);
    }

    @ApiOperation(value = "根据ID删除用户信息")
    @DeleteMapping("/user/{id}")
    @Log("根据ID删除用户信息")
    @RequiresPermissions("删除指定ID的信息")
    public ResponseEntity<ResultBean> deleteGoodById(@PathVariable Long id) {
        boolean flag = userService.deleteById(id);
        if (flag)
            return new ResponseEntity<>(ResultBean.success("删除成功"), HttpStatus.OK);
        return new ResponseEntity<>(ResultBean.success("删除失败！没有指定ID的用户"), HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "根据用户ID获得用户角色")
    @GetMapping("/user/role/{id}")
    public ResponseEntity<ResultBean> getRolesByUserId(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null)
            return new ResponseEntity<>(ResultBean.error("获取失败！没有指定ID的用户"), HttpStatus.BAD_REQUEST);
        else
            return new ResponseEntity<>(ResultBean.success(user.getRoleList()), HttpStatus.OK);

    }

    @ApiOperation(value = "根据用户ID删除用户角色")
    @PostMapping("/user/delete/{id}")
    public ResponseEntity<ResultBean> getRolesByUserId(@PathVariable Long id, @RequestBody @ApiParam("用户ID集合") List<Long> roleIds) {
        User user = userService.findById(id);
        int successNumber = 0;
        if (user == null)
            return new ResponseEntity<>(ResultBean.error("获取失败！没有指定ID的用户"), HttpStatus.BAD_REQUEST);
        else {
            for (Long roleId : roleIds) {
                if (!roleDao.findById(roleId).isPresent())
                    continue;
                Integer result = userAndRoleDao.deleteByUserIdAndRoleId(id, roleId);
                if (result != null && result > 0)
                    successNumber++;
            }
            return new ResponseEntity<>(ResultBean.success(new ResponseBean(roleIds.size(), successNumber)), HttpStatus.OK);
        }

    }

    @ApiOperation(value = "获取当前用户信息")
    @GetMapping("/user/currentUser")
    public ResponseEntity<ResultBean> getUser(@CurrentUser User user) {
        return new ResponseEntity<>(ResultBean.success(user), HttpStatus.OK);
    }

    @ApiOperation(value = "user登录")
    @PostMapping("/user/login")
    public ResponseEntity<ResultBean> login(@Valid @RequestBody @ApiParam(value = "用户信息json格式") Admin adminEntity, BindingResult error) {
        if (error.hasErrors())
            return new ResponseEntity<>(ResultBean.error(ValidatorUtil.getError(error)), HttpStatus.BAD_REQUEST);
        User admin = userService.findByName(adminEntity.getUsername());
        if (admin == null)
            admin = userService.findByTelephone(adminEntity.getUsername());
        if (admin == null)
            admin = userService.findByMail(adminEntity.getUsername());
        if (admin != null) {
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(admin.getName(), adminEntity.getPassword());
            SecurityUtils.getSubject().login(usernamePasswordToken);
            HttpHeaders responseHeaders = new HttpHeaders();
            Session session = SecurityUtils.getSubject().getSession();
            Serializable id = session.getId();
            session.setTimeout(Long.valueOf(SystemVersionArgs.tokenAliveTime));
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", id.toString());
            claims.put("userName", admin.getName());
            String token = id + " " + JWTTokenUtil.createJWTToken(claims, Long.valueOf(SystemVersionArgs.tokenAliveTime));
            redisUtil.sentinelSet(token, admin, Long.valueOf(SystemVersionArgs.tokenAliveTime));
            responseHeaders.set("ESLS", token);
            responseHeaders.set("Access-Control-Expose-Headers", "ESLS");
            List<User> users = new ArrayList<>();
            users.add(admin);
            List<UserVo> userVos = CopyUtil.copyUser(users);
            return new ResponseEntity<>(ResultBean.success(userVos.get(0)), responseHeaders, HttpStatus.OK);
        } else {
            //登陆失败
            return new ResponseEntity<>(ResultBean.error("用户名或密码错误"), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "用户注册")
    @PostMapping("/user/registry")
    public ResponseEntity<ResultBean> registryUser(@RequestBody @ApiParam("用户信息集合") UserVo userVo) throws MessagingException {
        User user = userService.registerUser(userVo);
        if (user != null)
            return new ResponseEntity<>(ResultBean.success(user), HttpStatus.OK);
        else
            return new ResponseEntity<>(ResultBean.error("失败 [用户名已经存在] "), HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "用户激活")
    @GetMapping("/user/activate")
    public ResponseEntity<ResultBean> activateUser(@RequestParam @ApiParam("用户激活码") String code) {
        User user = (User) redisUtil.sentinelGet(code, User.class);
        if (user == null)
            throw new ServiceException(ResultEnum.ACTIVATE_EXPIRE);
        user.setActivateStatus((byte) 1);
        User newUser = userService.saveOne(user);
        if (newUser == null)
            throw new ServiceException(ResultEnum.USER_SAVE_ERROR);
        userService.giveBasePermissionToUser(newUser);
        return new ResponseEntity<>(ResultBean.success("激活成功"), HttpStatus.OK);
    }

    @ApiOperation(value = "用户修改密码")
    @PostMapping("/user/changePassword")
    @Log("用户修改密码")
    @RequiresPermissions("用户修改密码")
    public ResponseEntity<ResultBean> changePassword(@RequestBody @ApiParam("用户信息集合") RequestBean requestBean, @RequestParam("新密码") String newPassword) {
        List<User> users = RequestBeanUtil.getUsersByRequestBean(requestBean);
        if (users == null || users.size() == 0)
            throw new ServiceException(ResultEnum.USER_NOT_EXIST);
        User user = users.get(0);
        ByteSource credentialsSalt = ByteSource.Util.bytes(user.getName());
        Object obj = new SimpleHash("MD5", newPassword, credentialsSalt, MD5Util.HASHITERATIONS);
        user.setPasswd(((SimpleHash) obj).toHex());
        userService.saveOne(user);
        return new ResponseEntity<>(ResultBean.success("修改成功"), HttpStatus.OK);
    }

    @ApiOperation(value = "向指定的用户手机号发送验证码")
    @PostMapping("/user/sendIdentifyCode")
    public ResponseEntity<ResultBean> sendIdentifyCode(@RequestParam @ApiParam("手机号") String phoneNumber) {
        User user = userService.findByTelephone(phoneNumber);
        if (user == null)
            throw new ServiceException(ResultEnum.USER_NOT_EXIST);
        return new ResponseEntity<>(ResultBean.success(MessageSender.sendAuthCode(phoneNumber)), HttpStatus.OK);
    }

    @ApiOperation(value = "验证手机验证码的正确性")
    @PostMapping("/user/identifyCode")
    public ResponseEntity<ResultBean> identifyCode(@RequestParam @ApiParam("手机号") String phoneNumber, @RequestParam @ApiParam("验证码") String code, @RequestParam(required = false) @ApiParam("用户重置的密码") String password) {
        String realCode = (String) redisUtil.sentinelGet(phoneNumber, String.class);
        if (realCode == null)
            throw new ServiceException(ResultEnum.VERTIFY_EXPIRE);
        User user = userService.findByTelephone(phoneNumber);
        Admin admin = new Admin(user.getName(), user.getRawPasswd());
        if (code.equals(realCode)) {
            if (password == null)
                return new ResponseEntity<>(ResultBean.success(login(admin, null)), HttpStatus.OK);
            else {
                RequestBean requestBean = new RequestBean();
                requestBean.getItems().add(new RequestItem("id", String.valueOf(user.getId())));
                return changePassword(requestBean, password);
            }
        } else
            return new ResponseEntity<>(ResultBean.error("验证失败"), HttpStatus.BAD_REQUEST);
    }

    @ApiOperation("切换指定ID的用户的状态（0禁用1启用）")
    @PutMapping("/user/status/{id}")
    @RequiresPermissions("切换状态")
    @Log("切换用户禁用状态")
    public ResponseEntity<ResultBean> forbidUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            RequestBean source = new RequestBean();
            ArrayList<RequestItem> sourceItems = new ArrayList<>();
            sourceItems.add(new RequestItem("id", String.valueOf(id)));
            source.setItems(sourceItems);
            RequestBean target = new RequestBean();
            ArrayList<RequestItem> targetItems = new ArrayList<>();
            // 0为禁用
            Byte status = user.getStatus();
            String str = String.valueOf(status != null && status == 1 ? 0 : 1);
            targetItems.add(new RequestItem("status", str));
            target.setItems(targetItems);
            Integer result = userService.updateByArrtribute(TableConstant.TABLE_USER, source, target);
            if (result != null && result > 0)
                return new ResponseEntity<>(ResultBean.success("切换状态成功 当前状态为：" + str), HttpStatus.OK);
            return new ResponseEntity<>(ResultBean.error("切换状态失败 当前状态为：" + status), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ResultBean.error("不存在此用户"), HttpStatus.BAD_REQUEST);
    }

    @ApiOperation("为指定ID的用户添加角色")
    @PostMapping("/user/addRole")
    @RequiresPermissions("为指定ID的用户添加角色")
    public ResponseEntity<ResultBean> addUserAndRole(@RequestBody @ApiParam("用户角色ID集合") RoleRequest roleRequest) {
        Map<Integer, Integer> sumResult = new HashMap<>(16);
        List<Long> ids = roleRequest.getIds();
        if (ids.size() != roleRequest.getCollectionIds().size())
            return new ResponseEntity<>(ResultBean.error("参数数量不匹配"), HttpStatus.BAD_REQUEST);
        for (int i = 0; i < ids.size(); i++) {
            int sum = 0;
            Long id = ids.get(i);
            User user = userService.findById(id);
            if (user == null) {
                sumResult.put(i + 1, 0);
                continue;
            }
            List<Long> collectionIds = roleRequest.getCollectionIds().get(i);
            for (Long roleId : collectionIds) {
                // 判断角色是否存在
                Optional<Role> role = roleService.findById(roleId);
                if (role.isPresent()) {
                    if (userAndRoleDao.insertByCondition(roleId, id) > 0)
                        sum++;
                }
            }
            sumResult.put(i + 1, sum);
        }
        return new ResponseEntity<>(ResultBean.success(sumResult), HttpStatus.OK);
    }
}
