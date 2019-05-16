package com.wdy.module.controller;

import com.wdy.module.common.constant.TableConstant;
import com.wdy.module.common.request.QueryAllBean;
import com.wdy.module.common.request.RoleRequest;
import com.wdy.module.common.response.ResponseHelper;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.dao.RoleAndPermissionDao;
import com.wdy.module.dao.UserAndRoleDao;
import com.wdy.module.entity.*;
import com.wdy.module.aop.Log;
import com.wdy.module.shiro.ShiroService;
import com.wdy.module.service.*;
import com.wdy.module.utils.ConditionUtil;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.*;


@Api(description = "角色管理")
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private UserAndRoleDao userAndRoleDao;
    @Autowired
    private RoleAndPermissionDao roleAndPermissionDao;
    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private ShiroService shiroService;

    @ApiOperation(value = "根据条件获取角色信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = " 查询条件 可为所有字段", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryString", value = "查询条件的字符串", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "数量", dataType = "int", paramType = "query")
    })
    @GetMapping("/roles")
    @RequiresPermissions("系统菜单")
    public ResponseEntity<ResultBean> getRoles(@RequestParam(required = false) String query, @RequestParam(required = false) String queryString, @Min(message = "data.page.min", value = 0) @RequestParam(required = false) Integer page, @Min(message = "data.count.min", value = 0) @RequestParam(required = false) Integer count) throws Exception {
        String result = ConditionUtil.judgeArgument(query, queryString, page, count);
        return roleService.getEntityList(QueryAllBean.builder().query(query).queryString(queryString).page(page).pagecount(count).result(result).serviceName("RoleService").build());
    }

    @ApiOperation(value = "获取指定ID的角色信息")
    @GetMapping("/role/{id}")
    @RequiresPermissions("获取指定ID的信息")
    public ResponseEntity<ResultBean> getRoleById(@PathVariable Long id) {
        Optional<Role> result = roleService.findById(id);
        return ResponseHelper.BooleanResultBean(result, "此ID角色不存在", result.isPresent());
    }

    @ApiOperation(value = "添加或修改角色信息")
    @PostMapping("/role")
    @RequiresPermissions("添加或修改信息")
    public ResponseEntity<ResultBean> saveRole(@ApiParam("角色描述") @RequestParam String name, @ApiParam("角色类型") @RequestParam String type) {
        Role role = new Role(name, type);
        return ResponseHelper.OK(roleService.saveOne(role));
    }

    @ApiOperation(value = "根据ID删除角色信息")
    @DeleteMapping("/role/{id}")
    @Log("根据ID删除角色信息")
    @RequiresPermissions("删除指定ID的信息")
    public ResponseEntity<ResultBean> deleteRoleById(@PathVariable Long id) {
        // 删除角色表
        boolean flag = roleService.deleteById(id);
        // 可批量删除
        List<Long> idList = new ArrayList<>();
        idList.add(id);
        // 删除user_role 和role_permission表中数据
        roleService.deleteByIdList(TableConstant.TABLE_USER_ROLE, "roleId", idList);
        roleService.deleteByIdList(TableConstant.TABLE_ROLE_PERMISSION, "roleId", idList);
        return ResponseHelper.BooleanResultBean("删除成功", "删除失败！没有指定ID的角色", flag);
    }

    @ApiOperation("为指定ID的角色添加权限")
    @PostMapping("/role/addPerm")
    @RequiresPermissions("为指定ID的角色添加权限")
    @Log("为指定ID的角色添加权限")
    public ResponseEntity<ResultBean> addRoleAndPermission(@RequestBody @ApiParam("角色权限ID集合") RoleRequest roleRequest) {
        Map<Integer, Integer> sumResult = new HashMap<>(16);
        List<Long> ids = roleRequest.getIds();
        if (ids.size() != roleRequest.getCollectionIds().size())
            return new ResponseEntity<>(ResultBean.error("参数数量不匹配"), HttpStatus.BAD_REQUEST);
        for (int i = 0; i < ids.size(); i++) {
            int sum = 0;
            Long id = ids.get(i);
            Optional<Role> role = roleService.findById(id);
            if (!role.isPresent()) {
                sumResult.put(i + 1, 0);
                continue;
            }
            List<Long> permissionList = roleRequest.getCollectionIds().get(i);
            for (Long permissonId : permissionList) {
                Optional<Permission> permission = permissionService.findById(permissonId);
                if (permission.isPresent()) {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRoleId(id);
                    rolePermission.setPermissionId(permissonId);
                    if (roleAndPermissionDao.findByRoleIdAndPermissionId(id, permissonId) == null) {
                        roleAndPermissionDao.save(rolePermission);
                        sum++;
                    }
                }
            }
            sumResult.put(i + 1, sum);
        }
        shiroService.updatePermission();
        return ResponseHelper.OK(sumResult);
    }

    @ApiOperation("删除指定ID的角色的对应权限")
    @DeleteMapping("/role/delPerm")
    @RequiresPermissions("删除指定ID的角色的对应权限")
    @Log("删除指定ID的角色的对应权限")
    public ResponseEntity<ResultBean> deleteRoleAndPermission(@RequestBody @ApiParam("角色权限ID集合") RoleRequest roleRequest) {
        Map<Integer, Integer> sumResult = new HashMap<>(16);
        List<Long> ids = roleRequest.getIds();
        if (ids.size() != roleRequest.getCollectionIds().size())
            return new ResponseEntity<>(ResultBean.error("参数数量不匹配"), HttpStatus.BAD_REQUEST);
        for (int i = 0; i < ids.size(); i++) {
            int sum = 0;
            Long id = ids.get(i);
            Optional<Role> role = roleService.findById(id);
            if (!role.isPresent()) {
                sumResult.put(i + 1, 0);
                continue;
            }
            List<Long> permissionList = roleRequest.getCollectionIds().get(i);
            for (Long permissonId : permissionList) {
                Optional<Permission> permission = permissionService.findById(permissonId);
                if (permission.isPresent()) {
                    RolePermission rolePermission = roleAndPermissionDao.findByRoleIdAndPermissionId(id, permissonId);
                    if (rolePermission != null) {
                        roleAndPermissionDao.deleteByRoleIdAndPermissionId(id, permissonId);
                        sum++;
                    }
                }
            }
            sumResult.put(i + 1, sum);
        }
        shiroService.updatePermission();
        return ResponseHelper.OK(sumResult);
    }
}
