package com.wdy.module.controller;

import com.wdy.module.common.constant.ArrtributeConstant;
import com.wdy.module.common.constant.TableConstant;
import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.entity.*;
import com.wdy.module.aop.Log;
import com.wdy.module.entity.Tag;
import com.wdy.module.netty.command.CommandConstant;
import com.wdy.module.service.GoodService;
import com.wdy.module.service.TagService;
import com.wdy.module.serviceUtil.SendCommandUtil;
import com.wdy.module.utils.ConditionUtil;
import com.wdy.module.serviceUtil.CopyUtil;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(description = "商品管理API")
@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
public class GoodController {
    @Autowired
    private TagService tagService;
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
    public ResponseEntity<ResultBean> getGoods(@RequestParam(required = false) String query, @RequestParam(required = false) String queryString, @Min(message = "data.page.min", value = 0) @RequestParam(required = false) Integer page, @RequestParam(required = false) @Min(message = "data.count.min", value = 0) Integer count) {
        String result = ConditionUtil.judgeArgument(query, queryString, page, count);
        if (result == null)
            return new ResponseEntity<>(ResultBean.error("参数组合有误 [query和queryString必须同时提供] [page和count必须同时提供]"), HttpStatus.BAD_REQUEST);
        // 带条件或查询
        if (query != null && query.contains(" ")) {
            List content = goodService.findAllBySql(TableConstant.TABLE_GOODS, "like", query, queryString, page, count, Good.class);
            List resultList = CopyUtil.copyGood(content);
            return new ResponseEntity<>(new ResultBean(resultList, resultList.size()), HttpStatus.OK);
        }
        // 查询全部
        if (result.equals(ConditionUtil.QUERY_ALL)) {
            List list = goodService.findAll();
            List content = CopyUtil.copyGood(list);
            return new ResponseEntity<>(new ResultBean(content, list.size()), HttpStatus.OK);
        }
        // 查询全部分页
        if (result.equals(ConditionUtil.QUERY_ALL_PAGE)) {
            List list = goodService.findAll();
            List content = goodService.findAll(page, count);
            List resultList = CopyUtil.copyGood(content);
            return new ResponseEntity<>(new ResultBean(resultList, list.size()), HttpStatus.OK);
        }
        // 带条件查询全部
        if (result.equals(ConditionUtil.QUERY_ATTRIBUTE_ALL)) {
            List content = goodService.findAllBySql(TableConstant.TABLE_GOODS, query, queryString, Good.class);
            List resultList = CopyUtil.copyGood(content);
            return new ResponseEntity<>(new ResultBean(resultList, resultList.size()), HttpStatus.OK);
        }
        // 带条件查询分页
        if (result.equals(ConditionUtil.QUERY_ATTRIBUTE_PAGE)) {
            List list = goodService.findAll();
            List content = goodService.findAllBySql(TableConstant.TABLE_GOODS, query, queryString, page, count, Good.class);
            List resultList = CopyUtil.copyGood(content);
            return new ResponseEntity<>(new ResultBean(resultList, list.size()), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResultBean.error("查询组合出错 函数未执行！"), HttpStatus.BAD_REQUEST);
    }

    @ApiOperation("根据多个字段搜索数据")
    @PostMapping("/goods/search")
    @RequiresPermissions("查询和搜索功能")
    public ResponseEntity<ResultBean> searchGoodsByConditon(@RequestParam String connection, @Min(message = "data.page.min", value = 0) @RequestParam Integer page, @RequestParam @Min(message = "data.count.min", value = 0) Integer count, @RequestBody @ApiParam(value = "查询条件json格式") RequestBean requestBean) {
        List<Good> goods = goodService.findAllBySql(TableConstant.TABLE_GOODS, connection, requestBean, page, count, Good.class);
        return new ResponseEntity<>(new ResultBean(CopyUtil.copyGood(goods)), HttpStatus.OK);
    }

    @ApiOperation(value = "获取指定ID的商品信息")
    @GetMapping("/goods/{id}")
    @Transactional
    @RequiresPermissions("获取指定ID的信息")
    public ResponseEntity<ResultBean> getGoodById(@PathVariable Long id) {
        Good good = goodService.findById(id);
        if (good == null)
            return new ResponseEntity<>(ResultBean.error("此ID商品不存在"), HttpStatus.BAD_REQUEST);
        List goods = new ArrayList<Good>();
        goods.add(good);
        return new ResponseEntity<>(new ResultBean(CopyUtil.copyGood(goods)), HttpStatus.OK);
    }

    // 上传商品时带特征图片
    @ApiOperation(value = "添加或修改商品信息")
    @PostMapping("/good")
    @RequiresPermissions("添加或修改信息")
    public ResponseEntity<ResultBean> saveGood(@RequestBody @ApiParam(value = "商品信息json格式") Good good) {
        return new ResponseEntity<>(new ResultBean(goodService.saveOne(good)), HttpStatus.OK);
    }

    @ApiOperation(value = "根据ID删除商品信息")
    @DeleteMapping("/good/{id}")
    @Log("根据ID删除商品信息")
    @RequiresPermissions("删除指定ID的信息")
    public ResponseEntity<ResultBean> deleteGoodById(@PathVariable Long id) {
        boolean flag = goodService.deleteById(id);
        if (flag) {
            List<Tag> tags = tagService.findByArrtribute(TableConstant.TABLE_TAGS, ArrtributeConstant.TAG_GOODID, String.valueOf(id), Tag.class);
            for (Tag tag : tags) {
                String contentType = CommandConstant.TAGBINDOVER;
                SendCommandUtil.sendCommandWithTags(tags, contentType, CommandConstant.COMMANDTYPE_TAG);
                tag.setGood(null);
                tagService.saveOne(tag);
            }
            return new ResponseEntity<>(ResultBean.success("删除成功"), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResultBean.success("删除失败！没有指定ID的商品"), HttpStatus.BAD_REQUEST);
    }

    @ApiOperation("对绑定商品的所有标签内容进行更新(可单个 批量) 不指定put参数则对商品数据waitUpdate字段为0的数据进行更新")
    @PutMapping("/good/update")
    @Log("对商品绑定的所有标签内容进行更新(可单个 批量 改价) ")
    @RequiresPermissions("商品改价")
    public ResponseEntity<ResultBean> updateGoods(@RequestBody(required = false) @ApiParam("商品信息集合") RequestBean requestBean) {
        if (requestBean == null || requestBean.getItems().size() == 0)
            return new ResponseEntity<>(new ResultBean(goodService.updateGoods(false)), HttpStatus.OK);
        else if (requestBean.getItems().size() > 0)
            return new ResponseEntity<>(new ResultBean(goodService.updateGoods(requestBean)), HttpStatus.OK);
        return new ResponseEntity<>(ResultBean.error("参数有误"), HttpStatus.OK);
    }

    @ApiOperation("通过商品属性获取其绑定的所有标签信息（连接符可取=或like）")
    @GetMapping("/good/binded")
    @RequiresPermissions("通过商品ID获取其绑定的所有标签信息")
    public ResponseEntity<ResultBean> getBindTags(@RequestParam String query, @RequestParam String connection, @RequestParam String queryString) {
        List<Tag> tags = goodService.getBindTags(query, connection, queryString);
        return new ResponseEntity<>(new ResultBean(CopyUtil.copyTag(tags)), HttpStatus.OK);
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
        boolean result = goodService.setScheduleTask(cron, rootFilePath, mode);
        if (result)
            return new ResponseEntity<>(new ResultBean("设置成功"), HttpStatus.OK);
        else
            return new ResponseEntity<>(new ResultBean("设置失败"), HttpStatus.BAD_REQUEST);

    }

    @ApiOperation("上传商品基本数据及变价数据文件(csv)")
    @PostMapping("/good/upload")
    @Log("上传商品基本数据及变价数据文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = "-1为商品基本数据 -2为商品变价数据", dataType = "int", paramType = "query")
    })
    @RequiresPermissions("上传商品基本数据及变价数据文件")
    public ResponseEntity<ResultBean> uploadGoodData(@ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile file, @RequestParam Integer mode) {
        boolean result = goodService.uploadGoodData(file, mode);
        if (result)
            return new ResponseEntity<>(new ResultBean("上传成功"), HttpStatus.OK);
        else
            return new ResponseEntity<>(new ResultBean("上传失败"), HttpStatus.BAD_REQUEST);
    }
}
