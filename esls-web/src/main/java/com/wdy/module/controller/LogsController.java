package com.wdy.module.controller;

import com.wdy.module.common.constant.TableConstant;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.entity.Logs;
import com.wdy.module.service.LogService;
import com.wdy.module.utils.ConditionUtil;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.*;

@RestController
@Api(description = "日志管理API")
@CrossOrigin(origins = "*",maxAge = 3600)
@Validated
public class LogsController {
    @Autowired
    private LogService logService;
    @ApiOperation(value = "根据条件获取日志信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件 可为所有字段 ", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryString", value = "查询条件的字符串", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "数量", dataType = "int", paramType = "query")
    })
    @GetMapping("/logs")
    @RequiresPermissions("系统菜单")
    public ResponseEntity<ResultBean> getLogs(@RequestParam(required = false) String query, @RequestParam(required = false) String queryString, @Min(message = "data.page.min", value = 0)@RequestParam(required = false) Integer page, @Min(message = "data.count.min", value = 0) @RequestParam(required = false) Integer count) {
        String result = ConditionUtil.judgeArgument(query, queryString, page, count);
        if(result==null)
            return new ResponseEntity<>(ResultBean.error("参数组合有误 [query和queryString必须同时提供] [page和count必须同时提供]"), HttpStatus.BAD_REQUEST);
        // 带条件或查询
        if(query!=null && query.contains(" ")){
            List content = logService.findAllBySql(TableConstant.TABLE_LOGS, "like", query, queryString, page, count, Logs.class);
            return new ResponseEntity<>(new ResultBean(content, content.size()), HttpStatus.OK);
        }
        // 查询全部
        if(result.equals(ConditionUtil.QUERY_ALL)) {
            List list = logService.findAll();
            return new ResponseEntity<>(new ResultBean(list, list.size()), HttpStatus.OK);
        }
        // 查询全部分页
        if(result.equals(ConditionUtil.QUERY_ALL_PAGE)){
            List list = logService.findAll();
            List content = logService.findAll(page, count);
            return new ResponseEntity<>(new ResultBean(content, list.size()), HttpStatus.OK);
        }
        // 带条件查询全部
        if(result.equals(ConditionUtil.QUERY_ATTRIBUTE_ALL)) {
            List content = logService.findAllBySql(TableConstant.TABLE_LOGS, query, queryString,Logs.class);
            return new ResponseEntity<>(new ResultBean(content, content.size()), HttpStatus.OK);
        }
        // 带条件查询分页
        if(result.equals(ConditionUtil.QUERY_ATTRIBUTE_PAGE)) {
            List list = logService.findAll();
            List content = logService.findAllBySql(TableConstant.TABLE_LOGS, query, queryString, page, count,Logs.class);
            return new ResponseEntity<>(new ResultBean(content, list.size()), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResultBean.error("查询组合出错 函数未执行！"), HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "获取指定ID的日志信息")
    @GetMapping("/log/{id}")
    @RequiresPermissions("获取指定ID的信息")
    public ResponseEntity<ResultBean> getLogById(@PathVariable Long id) {
        Optional<Logs> result = logService.findById(id);
        if(result.isPresent()) {
            ArrayList<Logs> list = new ArrayList<>();
            list.add(result.get());
            return new ResponseEntity<>(new ResultBean(list), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResultBean.error("此ID日志不存在"), HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "添加或修改日志信息")
    @PostMapping("/log")
    @RequiresPermissions("添加或修改信息")
    public ResponseEntity<ResultBean> saveLog(@RequestBody @ApiParam(value = "日志信息json格式")  Logs logs) {
        return new ResponseEntity<>(new ResultBean(logService.saveOne(logs)), HttpStatus.OK);
    }

    @ApiOperation(value = "根据ID删除日志信息")
    @DeleteMapping("/log/{id}")
    @RequiresPermissions("删除指定ID的信息")
    public ResponseEntity<ResultBean> deleteLogById(@PathVariable Long id) {
        boolean flag = logService.deleteById(id);
        if(flag)
            return new ResponseEntity<>(ResultBean.success("删除成功"), HttpStatus.OK);
        return new ResponseEntity<>(ResultBean.success("删除失败！没有指定ID的日志"), HttpStatus.BAD_REQUEST);
    }
}
