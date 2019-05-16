package com.wdy.module.controller;

import com.wdy.module.common.request.QueryAllBean;
import com.wdy.module.common.response.ResponseHelper;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.dao.RoleDao;
import com.wdy.module.entity.Permission;
import com.wdy.module.entity.Role;
import com.wdy.module.service.PermissionService;
import com.wdy.module.utils.ConditionUtil;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Optional;

@Api(description = "权限管理")
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class PermissionController {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RoleDao roleDao;

    @ApiOperation(value = "根据条件获取权限信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = " 查询条件 可为所有字段", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryString", value = "查询条件的字符串", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "数量", dataType = "int", paramType = "query")
    })
    @GetMapping("/permissions")
    @RequiresPermissions("系统菜单")
    public ResponseEntity<ResultBean> getPermissions(@RequestParam(required = false) String query, @RequestParam(required = false) String queryString, @Min(message = "data.page.min", value = 0) @RequestParam(required = false) Integer page, @Min(message = "data.count.min", value = 0) @RequestParam(required = false) Integer count) throws Exception {
        String result = ConditionUtil.judgeArgument(query, queryString, page, count);
        return permissionService.getEntityList(QueryAllBean.builder().query(query).queryString(queryString).page(page).pagecount(count).result(result).serviceName("PermissionService").build());
    }

    @ApiOperation(value = "获取指定ID的权限信息")
    @GetMapping("/permission/{id}")
    @RequiresPermissions("获取指定ID的信息")
    public ResponseEntity<ResultBean> getPermissionById(@PathVariable Long id) {
        Optional<Permission> result = permissionService.findById(id);
        return ResponseHelper.BooleanResultBean(result, "此ID权限不存在", result.isPresent());
    }

    //    @ApiOperation(value = "添加或修改权限信息")
//    @PostMapping("/permission")
//    @Log("添加或修改权限信息")
//    @RequiresPermissions("添加或修改信息")
//    public ResponseEntity<ResultBean> savePermission(@ApiParam(value = "权限信息描述") @RequestParam String name,@ApiParam(value = "权限信息url") @RequestParam String url) {
//        Permission permission = new Permission(name,url);
//        Permission result = permissionService.saveOne(permission);
//        shiroService.updatePermission();
//        return new ResponseEntity<>(new ResultBean(result),HttpStatus.OK);
//    }
//
//    @ApiOperation(value = "根据ID删除权限信息")
//    @DeleteMapping("/permission/{id}")
//    @Log("根据ID删除权限信息")
//    @RequiresPermissions("删除指定ID的信息")
//    public ResponseEntity<ResultBean> deletePermissionById(@PathVariable Long id) {
//        List<Long> idList = new ArrayList<>();
//        idList.add(id);
//        permissionService.deleteByIdList(TableConstant.TABLE_ROLE_PERMISSION,"permission_id",idList);
//        boolean flag = permissionService.deleteById(id);
//        if (flag)
//            return new ResponseEntity<>(ResultBean.success("删除成功"), HttpStatus.OK);
//        return new ResponseEntity<>(ResultBean.success("删除失败！没有指定ID的权限"), HttpStatus.BAD_REQUEST);
//    }
    @ApiOperation("根据角色ID获得权限")
    @GetMapping("/permission/role/{id}")
    @RequiresPermissions("根据角色ID获得权限")
    public ResponseEntity<ResultBean> getPermissionByRoleId(@PathVariable Long id) {
        Role role = roleDao.findById(id).get();
        return ResponseHelper.BooleanResultBean(role.getPermissions(), "不存在此用户", role != null);
    }
}
