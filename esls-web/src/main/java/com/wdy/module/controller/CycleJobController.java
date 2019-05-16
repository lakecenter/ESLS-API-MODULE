package com.wdy.module.controller;

import com.wdy.module.common.exception.ResultEnum;
import com.wdy.module.common.request.QueryAllBean;
import com.wdy.module.common.response.ResponseHelper;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.cycleJob.DynamicTask;
import com.wdy.module.entity.CycleJob;
import com.wdy.module.common.exception.ServiceException;
import com.wdy.module.service.CycleJobService;
import com.wdy.module.utils.ConditionUtil;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Arrays;

@Api(description = "定期任务工具类")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Validated
public class CycleJobController {
    @Autowired
    private CycleJobService cycleJobService;
    @Autowired
    private DynamicTask dynamicTask;

    @ApiOperation(value = "根据条件获取定期任务信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件 可为所有字段 分隔符为单个空格 ", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryString", value = "查询条件的字符串", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "数量", dataType = "int", paramType = "query")
    })
    @GetMapping("/cyclejobs")
    @RequiresPermissions("系统菜单")
    public ResponseEntity<ResultBean> getGoods(@RequestParam(required = false) String query, @RequestParam(required = false) String queryString, @Min(message = "data.page.min", value = 0) @RequestParam(required = false) Integer page, @RequestParam(required = false) @Min(message = "data.count.min", value = 0) Integer count) throws Exception {
        String result = ConditionUtil.judgeArgument(query, queryString, page, count);
        return cycleJobService.getEntityList(QueryAllBean.builder().query(query).queryString(queryString).page(page).pagecount(count).result(result).serviceName("CycleJobService").build());
    }

    @ApiOperation(value = "获取指定ID的定期任务信息")
    @GetMapping("/cyclejobs/{id}")
    @Transactional
    @RequiresPermissions("获取指定ID的信息")
    public ResponseEntity<ResultBean> getGoodById(@PathVariable Long id) {
        CycleJob cycleJob = cycleJobService.findById(id);
        return ResponseHelper.OK(Arrays.asList(cycleJob));
    }

    @ApiOperation(value = "添加或修改定期任务信息")
    @PostMapping("/cyclejob")
    @RequiresPermissions("添加或修改信息")
    public ResponseEntity<ResultBean> saveGood(@RequestBody @ApiParam(value = "定期任务json格式") CycleJob cycleJob) {
        if (cycleJob.getMode() == -1 && cycleJob.getType() != 7 && cycleJob.getId() == 0)
            throw new ServiceException(ResultEnum.CYCLEJOB_NOT_ALLOW_CHANGE_BASE_GOOD_JOB);
        if (cycleJob.getMode() == -2 && cycleJob.getType() != 8 && cycleJob.getId() == 0)
            throw new ServiceException(ResultEnum.CYCLEJOB_NOT_ALLOW_CHANGE_CHANGE_GOOD_JOB);
        CycleJob result = cycleJobService.saveOne(cycleJob);
        dynamicTask.restartFutures();
        return ResponseHelper.OK(result);
    }

    @ApiOperation(value = "根据ID删除定期任务信息")
    @DeleteMapping("/cyclejob/{id}")
    @RequiresPermissions("删除指定ID的信息")
    public ResponseEntity<ResultBean> deleteGoodById(@PathVariable Long id) {
        CycleJob cycleJob = cycleJobService.findById(id);
        if (cycleJob.getMode() == -1 || cycleJob.getMode() == -2)
            throw new ServiceException(ResultEnum.CYCLEJOB_NOT_ALLOW_DELETE);
        boolean flag = cycleJobService.deleteById(id);
        if (flag) {
            dynamicTask.restartFutures();
        }
        return ResponseHelper.BooleanResultBean("删除成功", "删除失败！没有指定ID的定期任务", flag);
    }
}
