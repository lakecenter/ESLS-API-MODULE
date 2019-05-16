package com.wdy.module.controller;

import com.wdy.module.common.constant.TableConstant;
import com.wdy.module.common.request.QueryAllBean;
import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.common.response.ResponseHelper;
import com.wdy.module.entity.*;
import com.wdy.module.aop.Log;
import com.wdy.module.entity.Tag;
import com.wdy.module.service.GoodService;
import com.wdy.module.utils.ConditionUtil;
import com.wdy.module.serviceUtil.CopyUtil;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import java.util.Arrays;
import java.util.List;

@RestController
@Api(description = "商品管理API")
@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
public class GoodController {
    @Autowired
    private GoodService goodService;

    @ApiOperation(value = "根据条件获取商品信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件 可为所有字段 分隔符为单个空格 ", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryString", value = "查询条件的字符串", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "数量", dataType = "int", paramType = "query")
    })
    @GetMapping("/goods")
    @RequiresPermissions("系统菜单")
    public ResponseEntity<ResultBean> getGoods(@RequestParam(required = false) String query, @RequestParam(required = false) String queryString, @Min(message = "data.page.min", value = 0) @RequestParam(required = false) Integer page, @RequestParam(required = false) @Min(message = "data.count.min", value = 0) Integer count) throws Exception {
        String result = ConditionUtil.judgeArgument(query, queryString, page, count);
        return goodService.getEntityList(QueryAllBean.builder().query(query).queryString(queryString).page(page).pagecount(count).result(result).serviceName("GoodService").build());
    }

    @ApiOperation("根据多个字段搜索数据")
    @PostMapping("/goods/search")
    @RequiresPermissions("查询和搜索功能")
    public ResponseEntity<ResultBean> searchGoodsByConditon(@RequestParam String connection, @Min(message = "data.page.min", value = 0) @RequestParam Integer page, @RequestParam @Min(message = "data.count.min", value = 0) Integer count, @RequestBody @ApiParam(value = "查询条件json格式") RequestBean requestBean) {
        List<Good> goods = goodService.findAllBySql(TableConstant.TABLE_GOODS, connection, requestBean, page, count, Good.class);
        return ResponseHelper.OK(CopyUtil.copyGood(goods));
    }

    @ApiOperation(value = "获取指定ID的商品信息")
    @GetMapping("/goods/{id}")
    @Transactional
    @RequiresPermissions("获取指定ID的信息")
    public ResponseEntity<ResultBean> getGoodById(@PathVariable Long id) {
        return ResponseHelper.OK(CopyUtil.copyGood(Arrays.asList(goodService.findById(id))));
    }

    // 上传商品时带特征图片
    @ApiOperation(value = "添加或修改商品信息")
    @PostMapping("/good")
    @RequiresPermissions("添加或修改信息")
    public ResponseEntity<ResultBean> saveGood(@RequestBody @ApiParam(value = "商品信息json格式") Good good) {
        return ResponseHelper.OK(goodService.saveOne(good));
    }

    @ApiOperation(value = "根据ID删除商品信息")
    @DeleteMapping("/good/{id}")
    @Log("根据ID删除商品信息")
    @RequiresPermissions("删除指定ID的信息")
    public ResponseEntity<ResultBean> deleteGoodById(@PathVariable Long id) {
        boolean flag = goodService.deleteById(id);
        return ResponseHelper.BooleanResultBean("删除成功", "删除失败！没有指定ID的商品", flag);
    }

    @ApiOperation("对绑定商品的所有标签内容进行更新(可单个 批量) 不指定put参数则对商品数据waitUpdate字段为0的数据进行更新")
    @PutMapping("/good/update")
    @Log("对商品绑定的所有标签内容进行更新(可单个 批量 改价) ")
    @RequiresPermissions("商品改价")
    public ResponseEntity<ResultBean> updateGoods(@RequestBody(required = false) @ApiParam("商品信息集合") RequestBean requestBean) {
        if (requestBean == null || requestBean.getItems().size() == 0)
            return ResponseHelper.OK(goodService.updateGoods(false));
        else if (requestBean.getItems().size() > 0)
            return ResponseHelper.OK(goodService.updateGoods(requestBean));
        return ResponseHelper.BadRequest("参数有误");
    }

    @ApiOperation("通过商品属性获取其绑定的所有标签信息（连接符可取=或like）")
    @GetMapping("/good/binded")
    @RequiresPermissions("通过商品ID获取其绑定的所有标签信息")
    public ResponseEntity<ResultBean> getBindTags(@RequestParam String query, @RequestParam String connection, @RequestParam String queryString) {
        List<Tag> tags = goodService.getBindTags(query, connection, queryString);
        return ResponseHelper.OK(CopyUtil.copyTag(tags));
    }

    @ApiOperation("设置商品基本数据和商品变价文件路径及cron表达式（定期任务）")
    @GetMapping("/good/schedule")
    @Log("设置商品基本数据和商品变价文件路径及cron表达式（定期任务）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cron", value = "cron表达式 ", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "rootFilePath", value = "文件根路径", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "mode", value = "-1为商品基本数据 -2为商品变价数据", dataType = "int", paramType = "query")
    })
    @RequiresPermissions("设置商品基本数据和商品变价文件路径及cron表达式（定期任务）")
    public ResponseEntity<ResultBean> setSchedule(@RequestParam(required = false) String cron, @RequestParam(required = false) String rootFilePath, @RequestParam Integer mode) {
        boolean flag = goodService.setScheduleTask(cron, rootFilePath, mode);
        return ResponseHelper.BooleanResultBean("设置成功", "设置失败", flag);
    }

    @ApiOperation("上传商品基本数据及变价数据文件(csv)")
    @PostMapping("/good/upload")
    @Log("上传商品基本数据及变价数据文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = "-1为商品基本数据 -2为商品变价数据", dataType = "int", paramType = "query")
    })
    @RequiresPermissions("上传商品基本数据及变价数据文件")
    public ResponseEntity<ResultBean> uploadGoodData(@ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile file, @RequestParam Integer mode) {
        boolean flag = goodService.uploadGoodData(file, mode);
        return ResponseHelper.BooleanResultBean("上传成功", "上传失败", flag);
    }
}
