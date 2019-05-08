package com.wdy.module.controller;

import com.wdy.module.common.constant.TableConstant;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.dto.DispmsVo;
import com.wdy.module.entity.Dispms;
import com.wdy.module.aop.Log;
import com.wdy.module.service.DispmsService;
import com.wdy.module.serviceUtil.CopyUtil;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.*;

@RestController
@Api(description = "小样式样式API[字体颜色和背景颜色取值为（黑（0），白（1），红（2））]")
//实现跨域注解
@CrossOrigin(origins = "*", maxAge = 3600)
public class DispmsController {
    @Autowired
    private DispmsService dispmsService;
    @ApiOperation(value = "根据条件获取样式块信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件 可为所有字段", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryString", value = "查询条件的字符串", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码",dataType = "int",  paramType = "query"),
            @ApiImplicitParam(name = "count", value = "数量",dataType = "int",  paramType = "query")
    })
    @GetMapping("/dispms")
    @RequiresPermissions("系统菜单")
    public ResponseEntity<ResultBean> getDispmses(@RequestParam(required = false) String query, @RequestParam(required = false) String queryString, @Min(message = "data.page.min", value = 0)@RequestParam Integer page, @Min(message = "data.count.min", value = 0) @RequestParam Integer count){
        if(query!=null && queryString!=null) {
            List<Dispms> list = dispmsService.findAllBySql(TableConstant.TABLE_DISPMS, query, queryString, page, count,Dispms.class);
            return new ResponseEntity<>(new ResultBean(list,list.size()), HttpStatus.OK);
        }
        List<Dispms> list = dispmsService.findAll();
        List<Dispms> content = dispmsService.findAll(page,count);
        return new ResponseEntity<>(new ResultBean(CopyUtil.copyDispms(content),list.size()), HttpStatus.OK);
    }
    @ApiOperation(value = "获取指定ID的样式块信息")
    @GetMapping("/dispm/{id}")
    @RequiresPermissions("获取指定ID的信息")
    public ResponseEntity<ResultBean> getDispmsById(@PathVariable Long id){
        Optional<Dispms> dispms = dispmsService.findById(id);
        List<Dispms> content = new ArrayList<>();
        content.add(dispms.get());
        if(dispms.isPresent())
            return new ResponseEntity<>(new ResultBean(CopyUtil.copyDispms(content)), HttpStatus.OK);
        return new ResponseEntity<>(ResultBean.error("此ID样式块不存在"), HttpStatus.BAD_REQUEST);
    }
    @ApiOperation(value = "添加或修改样式块信息")
    @PostMapping("/dispm")
    @RequiresPermissions("添加或修改信息")
    public ResponseEntity<ResultBean> saveDispms(@RequestBody @ApiParam(value="样式块信息json格式") DispmsVo dispmsVo){
        Dispms dispms = new Dispms();
        BeanUtils.copyProperties(dispmsVo,dispms);
        return new ResponseEntity<>(new ResultBean(dispmsService.saveOne(dispms)), HttpStatus.OK);
    }
    @ApiOperation(value = "根据ID删除样式块信息")
    @DeleteMapping("/dispm/{id}")
    @RequiresPermissions("删除指定ID的信息")
    public ResponseEntity<ResultBean> deleteDispmsById(@PathVariable Long id) {
        boolean flag = dispmsService.deleteById(id);
        if(flag)
            return new ResponseEntity<>(ResultBean.success("删除成功"), HttpStatus.OK);
        return new ResponseEntity<>(ResultBean.success("删除失败！没有指定ID的样式块"), HttpStatus.BAD_REQUEST);
    }
}
