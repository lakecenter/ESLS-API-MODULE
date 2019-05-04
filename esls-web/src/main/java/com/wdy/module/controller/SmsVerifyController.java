package com.wdy.module.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.wdy.module.aop.Log;
import com.wdy.module.aop.Pass;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.config.ResponseHelper;
import com.wdy.module.config.ResponseModel;
import com.wdy.module.entity.SmsVerify;
import com.wdy.module.mybatis.mybatisService.ISmsVerifyService;
import com.wdy.module.utils.ConditionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * <p>
 * 验证码发送记录 前端控制器
 * </p>
 *
 * @author liugh123
 * @since 2018-06-25
 */
@RestController
@Api(description = "短信模块")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SmsVerifyController {

    @Autowired
    private ISmsVerifyService smsVerifyService;

    @ApiOperation(value = "根据条件获取短信信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件 可为所有字段 ", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryString", value = "查询条件的字符串", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "数量", dataType = "int", paramType = "query")
    })
    @GetMapping("smsVerifys")
    @RequiresPermissions("系统菜单")
    public ResponseEntity<ResultBean> getAll(@RequestParam(required = false) String query, @RequestParam(required = false) String queryString, @Min(message = "data.page.min", value = 0) @RequestParam(required = false) Integer page, @Min(message = "data.count.min", value = 0) @RequestParam(required = false) Integer count) {
        String result = ConditionUtil.judgeArgument(query, queryString, page, count);
        if (result == null)
            return new ResponseEntity<>(ResultBean.error("参数组合有误 [query和queryString必须同时提供] [page和count必须同时提供]"), HttpStatus.BAD_REQUEST);
        // 查询全部
        if (result.equals(ConditionUtil.QUERY_ALL)) {
            List list = smsVerifyService.findAll();
            return new ResponseEntity<>(new ResultBean(list, list.size()), HttpStatus.OK);
        }
        // 查询全部分页
        if (result.equals(ConditionUtil.QUERY_ALL_PAGE)) {
            List list = smsVerifyService.findAll();
            Page<SmsVerify> smsVerifyPage = smsVerifyService.selectPage(new Page<>(page, count));
            return new ResponseEntity<>(new ResultBean(smsVerifyPage.getRecords(), list.size()), HttpStatus.OK);
        }
        // 带条件查询全部
        if (result.equals(ConditionUtil.QUERY_ATTRIBUTE_ALL)) {
            List list = smsVerifyService.findAll();
            Wrapper<SmsVerify> entity = new EntityWrapper<>();
            entity.like(query, queryString);
            List<SmsVerify> smsVerifies = smsVerifyService.selectList(entity);
            return new ResponseEntity<>(new ResultBean(smsVerifies, list.size()), HttpStatus.OK);
        }
        // 带条件查询分页
        if (result.equals(ConditionUtil.QUERY_ATTRIBUTE_PAGE)) {
            List list = smsVerifyService.findAll();
            Wrapper<SmsVerify> queryWrapper = new EntityWrapper<>();
            queryWrapper.like(query, queryString).orderBy("id", false);
            Page<SmsVerify> smsVerifyPage = smsVerifyService.selectPage(new Page<>(page, count), queryWrapper);
            return new ResponseEntity<>(new ResultBean(smsVerifyPage.getRecords(), list.size()), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResultBean.error("查询组合出错 函数未执行！"), HttpStatus.BAD_REQUEST);
    }

    @ApiOperation("查询")
    @RequiresPermissions("查询和搜索功能")
    @GetMapping("/smsVerify/{id}")
    public ResponseModel findById(@PathVariable Long id) {
        SmsVerify smsVerify = smsVerifyService.selectById(id);
        return ResponseHelper.buildResponseModel(smsVerify);
    }

    @ApiOperation("删除")
    @RequiresPermissions("删除指定ID的信息")
    @DeleteMapping("/smsVerify/{id}")
    public ResponseModel deleteById(@PathVariable Long id) {
        return ResponseHelper.buildResponseModel(smsVerifyService.deleteById(id));
    }

    @ApiOperation("添加或修改")
    @RequiresPermissions("添加或修改信息")
    @PostMapping("/smsVerify")
    public ResponseModel save(@RequestBody SmsVerify smsVerify) {
        return ResponseHelper.buildResponseModel(smsVerifyService.insertOrUpdate(smsVerify));
    }

    /**
     * smsType 有四种类型：REG/注册账号 FINDPASSWORD/修改密码 AUTH/登陆验证 MODIFYINFO/修改账号
     *
     * @param smsType
     * @param mobile
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "获取验证码接口", notes = "路径参数,不需要Authorization")
    @GetMapping("/smsVerify/{smsType}/{mobile}")
    @Pass
    @Log(action = "getCaptcha", modelName = "Sms", value = "获取短信验证码接口")
    public ResponseModel getCaptcha(@PathVariable String smsType, @PathVariable String mobile) throws Exception {
        return ResponseHelper.buildResponseModel(smsVerifyService.addAndGetMobileAndCaptcha(smsType, mobile));
    }


    @ApiOperation(value = "验证码验证接口", notes = "请求参数,不需要Authorization")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "smsType", value = "验证码类型"
                    , required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "mobile", value = "手机号"
                    , required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "captcha", value = "验证码"
                    , required = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/smsVerify/captcha/check")
    public ResponseModel captchaCheck(@RequestParam String smsType,
                                      @RequestParam String mobile, @RequestParam String captcha) throws Exception {
        smsVerifyService.captchaCheck(mobile, smsType, captcha);
        return ResponseHelper.buildResponseModel(true);
    }
}

