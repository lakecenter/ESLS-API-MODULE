package com.wdy.module.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.wdy.module.common.exception.ResultEnum;
import com.wdy.module.common.exception.ServiceException;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.common.response.ResponseHelper;
import com.wdy.module.common.response.ResponseModel;
import com.wdy.module.entity.OperationLog;
import com.wdy.module.mybatis.mybatisService.IOperationLogService;
import com.wdy.module.utils.ConditionUtil;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * @program: esls-parent
 * @description:
 * @author: dongyang_wu
 * @create: 2019-04-27 18:47
 */
@RestController
@Api(description = "日志模块")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OperationLogController {
    @Autowired
    private IOperationLogService operationLogService;

    @ApiOperation(value = "根据条件获取日志信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件 可为所有字段 ", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryString", value = "查询条件的字符串", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "数量", dataType = "int", paramType = "query")
    })
    @GetMapping("/operationLogs")
    @RequiresPermissions("系统菜单")
    public ResponseEntity<ResultBean> getAll(@RequestParam(required = false) String query, @RequestParam(required = false) String queryString, @Min(message = "data.page.min", value = 0) @RequestParam(required = false) Integer page, @Min(message = "data.count.min", value = 0) @RequestParam(required = false) Integer count) {
        String result = ConditionUtil.judgeArgument(query, queryString, page, count);
        if (result == null)
            throw new ServiceException(ResultEnum.QUERY_LIST_ARGS_ERROR);
        // 查询全部
        if (result.equals(ConditionUtil.QUERY_ALL)) {
            List list = operationLogService.findAll();
            return ResponseHelper.OK(list, list.size());
        }
        // 查询全部分页
        if (result.equals(ConditionUtil.QUERY_ALL_PAGE)) {
            List list = operationLogService.findAll();
            Page smsVerifyPage = operationLogService.selectPage(new Page<>(page, count,"createTime",false));
            return ResponseHelper.OK(smsVerifyPage.getRecords(), list.size());
        }
        // 带条件查询全部
        if (result.equals(ConditionUtil.QUERY_ATTRIBUTE_ALL)) {
            List list = operationLogService.findAll();
            Wrapper<OperationLog> entity = new EntityWrapper<>();
            entity.like(query, queryString).orderBy("createTime", false);
            List smsVerifies = operationLogService.selectList(entity);
            return ResponseHelper.OK(smsVerifies, list.size());
        }
        // 带条件查询分页
        if (result.equals(ConditionUtil.QUERY_ATTRIBUTE_PAGE)) {
            List list = operationLogService.findAll();
            Wrapper<OperationLog> queryWrapper = new EntityWrapper<>();
            queryWrapper.like(query, queryString).orderBy("createTime", false);
            Page smsVerifyPage = operationLogService.selectPage(new Page<>(page, count), queryWrapper);
            return ResponseHelper.OK(smsVerifyPage.getRecords(), list.size());
        }
        return ResponseHelper.BadRequest("查询组合出错 函数未执行！");

    }

    @ApiOperation("查询")
    @RequiresPermissions("查询和搜索功能")
    @GetMapping("/operationLog/{id}")
    public ResponseModel findById(@PathVariable Long id) {
        OperationLog operationLog = operationLogService.selectById(id);
        return ResponseHelper.buildResponseModel(operationLog);
    }

    @ApiOperation("删除")
    @RequiresPermissions("删除指定ID的信息")
    @DeleteMapping("/operationLog/{id}")
    public ResponseModel deleteById(@PathVariable Long id) {
        return ResponseHelper.buildResponseModel(operationLogService.deleteById(id));
    }

}