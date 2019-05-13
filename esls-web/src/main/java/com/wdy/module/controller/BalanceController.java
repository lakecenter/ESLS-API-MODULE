package com.wdy.module.controller;

import com.wdy.module.aop.Log;
import com.wdy.module.common.constant.TableConstant;
import com.wdy.module.common.request.QueryAllBean;
import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.common.response.ResponseHelper;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.entity.Balance;
import com.wdy.module.netty.command.CommandConstant;
import com.wdy.module.service.BalanceService;
import com.wdy.module.serviceUtil.RequestBeanUtil;
import com.wdy.module.serviceUtil.SendCommandUtil;
import com.wdy.module.utils.*;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.wdy.module.entity.Tag;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Api(description = "电子秤管理API")
@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
public class BalanceController {
    @Autowired
    private BalanceService balanceService;

    @ApiOperation(value = "根据条件获取电子秤信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件 可为所有字段 ", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryString", value = "查询条件的字符串", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "数量", dataType = "int", paramType = "query")
    })
    @GetMapping("/balances")
    @RequiresPermissions("系统菜单")
    public ResponseEntity<ResultBean> getLogs(@RequestParam(required = false) String query, @RequestParam(required = false) String queryString, @Min(message = "data.page.min", value = 0) @RequestParam(required = false) Integer page, @Min(message = "data.count.min", value = 0) @RequestParam(required = false) Integer count) throws Exception {
        String result = ConditionUtil.judgeArgument(query, queryString, page, count);
        return balanceService.getEntityList(QueryAllBean.builder().query(query).queryString(queryString).page(page).pagecount(count).result(result).serviceName("BalanceService").build());
    }

    @ApiOperation(value = "获取指定ID的电子秤信息")
    @GetMapping("/balance/{id}")
    @RequiresPermissions("获取指定ID的信息")
    public ResponseEntity<ResultBean> getLogById(@PathVariable Long id) {
        Balance result = balanceService.findById(id);
        return ResponseHelper.buildSuccessResultBean(result);
    }

    @ApiOperation(value = "添加或修改电子秤信息")
    @PostMapping("/balance")
    @RequiresPermissions("添加或修改信息")
    public ResponseEntity<ResultBean> saveLog(@RequestBody @ApiParam(value = "电子秤信息json格式") Balance balance) {
        return ResponseHelper.buildSuccessResultBean(balanceService.saveOne(balance));
    }

    @ApiOperation(value = "根据ID删除电子秤信息")
    @DeleteMapping("/balance/{id}")
    @RequiresPermissions("删除指定ID的信息")
    public ResponseEntity<ResultBean> deleteLogById(@PathVariable Long id) {
        boolean flag = balanceService.deleteById(id);
        return ResponseHelper.buildBooleanResultBean("删除成功", "删除失败！没有指定ID的电子秤信息", flag);
    }

    @ApiOperation(value = "获取指定标签连接的电子秤的计量数据")
    @GetMapping("/balance/data")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = "0为获取计量数据 1为电子秤置零 2为电子秤去皮 3为获取电子秤电量", dataType = "int", paramType = "query")
    })
    @RequiresPermissions("使用电子秤相关API")
    @Log("使用电子秤相关API")
    public ResponseEntity<ResultBean> sendGetBalance(@RequestBody @ApiParam("标签信息集合") RequestBean requestBean, @RequestParam Integer mode) {
        ResponseBean responseBean = new ResponseBean(0, 0);
        List<Tag> tags = RequestBeanUtil.getTagsByRequestBean(requestBean);
        if (mode == 0) {
            responseBean = SendCommandUtil.sendCommandWithTags(tags, CommandConstant.GETBALANCE, CommandConstant.COMMANDTYPE_TAG, false);
        } else if (mode == 1) {
            responseBean = SendCommandUtil.sendCommandWithTags(tags, CommandConstant.BALANCETOZERO, CommandConstant.COMMANDTYPE_TAG, false);
        } else if (mode == 2) {
            responseBean = SendCommandUtil.sendCommandWithTags(tags, CommandConstant.BALANCETOFLAY, CommandConstant.COMMANDTYPE_TAG, false);
        } else if (mode == 3) {
            responseBean = SendCommandUtil.sendCommandWithTags(tags, CommandConstant.GETBALANCEPOWER, CommandConstant.COMMANDTYPE_TAG, false);
        }
        return ResponseHelper.buildSuccessResultBean(responseBean);
    }
}
