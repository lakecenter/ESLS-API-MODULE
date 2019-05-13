package com.wdy.module.controller;

import com.wdy.module.aop.AccessLimit;
import com.wdy.module.common.constant.*;
import com.wdy.module.common.request.QueryAllBean;
import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.common.response.ResponseHelper;
import com.wdy.module.dao.GoodDao;
import com.wdy.module.dto.TagVo;
import com.wdy.module.entity.*;
import com.wdy.module.entity.Tag;
import com.wdy.module.aop.Log;
import com.wdy.module.rabbitMq.RabbiMqSendBean;
import com.wdy.module.service.StyleService;
import com.wdy.module.service.TagService;
import com.wdy.module.serviceUtil.*;
import com.wdy.module.utils.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.*;

@RestController
@Api(description = "标签管理API")
@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
@Slf4j
public class TagController {
    @Autowired
    private TagService tagService;
    @Autowired
    private StyleService styleService;
    @Autowired
    private GoodDao goodDao;
//    @Autowired
//    private RabbitMqSender rabbitMqSender;

    @ApiOperation(value = "根据条件获取标签信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = " 查询条件 可为所有字段 分隔符为单个空格", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryString", value = "查询条件的字符串", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "数量", dataType = "int", paramType = "query")
    })
    @GetMapping("/tags")
    @RequiresPermissions("系统菜单")
    public ResponseEntity<ResultBean> getTags(@RequestParam(required = false) String query, @RequestParam(required = false) String queryString, @Min(message = "data.page.min", value = 0) @RequestParam(required = false) Integer page, @Min(message = "data.count.min", value = 0) @RequestParam(required = false) Integer count) throws Exception {
        String result = ConditionUtil.judgeArgument(query, queryString, page, count);
        return tagService.getEntityList(QueryAllBean.builder().query(query).queryString(queryString).page(page).pagecount(count).result(result).serviceName("TagService").build());
    }

    @ApiOperation(value = "获取指定ID的标签信息")
    @GetMapping("/tag/{id}")
    @RequiresPermissions("获取指定ID的信息")
    public ResponseEntity<ResultBean> getTagById(@PathVariable Long id) {
        Optional<Tag> result = tagService.findById(id);
        return ResponseHelper.buildSuccessResultBean(CopyUtil.copyTag(Arrays.asList(result.get())));
    }

    @ApiOperation(value = "添加或修改标签信息")
    @PostMapping("/tag")
    @RequiresPermissions("添加或修改信息")
    public ResponseEntity<ResultBean> saveTag(@RequestBody @ApiParam(value = "标签信息json格式") TagVo tagVo) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagVo, tag);
        // 绑定商品
        if (tagVo.getGoodId() != 0) {
            Good good = new Good();
            good.setId(tagVo.getGoodId());
            tag.setGood(good);
        }
        // 绑定样式
        if (tagVo.getStyleId() != 0) {
            Style style = new Style();
            style.setId(tagVo.getStyleId());
            tag.setStyle(style);
        }
        // 绑定路由
        if (tagVo.getRouterId() != 0) {
            Router router = new Router();
            router.setId(tagVo.getRouterId());
            tag.setRouter(router);
        }
        return ResponseHelper.buildSuccessResultBean(tagService.saveOne(tag));
    }

    @ApiOperation(value = "根据ID删除标签信息")
    @DeleteMapping("/tag/{id}")
    @Log("根据ID删除标签信息")
    @RequiresPermissions("删除指定ID的信息")
    public ResponseEntity<ResultBean> deleteTagById(@PathVariable Long id) {
        boolean flag = tagService.deleteById(id);
        return ResponseHelper.buildBooleanResultBean("删除成功", "删除失败！没有指定ID的标签", flag);
    }

    @ApiOperation("根据多个字段搜索数据")
    @PostMapping("/tags/search")
    @RequiresPermissions("查询和搜索功能")
    public ResponseEntity<ResultBean> searchTagsByConditon(@RequestParam String connection, @Min(message = "data.page.min", value = 0) @RequestParam Integer page, @RequestParam @Min(message = "data.count.min", value = 0) Integer count, @RequestBody @ApiParam(value = "查询条件json格式") RequestBean requestBean) {
        List<Tag> tags = tagService.findAllBySql(TableConstant.TABLE_TAGS, connection, requestBean, page, count, Tag.class);
        return ResponseHelper.buildSuccessResultBean(CopyUtil.copyTag(tags));
    }

    @ApiOperation(value = "自定义属性----标签和商品绑定和取消绑定")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sourceArgs1", value = "商品绑定属性", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ArgsString1", value = "商品绑定属性值", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "sourceArgs2", value = "标签绑定属性", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ArgsString2", value = "标签绑定属性值", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "mode", value = "2换绑1绑定0取消绑定", required = true, dataType = "String", paramType = "query")
    })
    @PutMapping("/tag/bind")
    @Log("标签和商品绑定和取消绑定")
    @AccessLimit(perSecond = 1)
    public ResponseEntity<ResultBean> bindGoodAndTag(@RequestParam String sourceArgs1, @RequestParam String ArgsString1, @RequestParam String sourceArgs2, @RequestParam String ArgsString2, String mode) {
        return tagService.bindGoodAndTag(sourceArgs1, ArgsString1, sourceArgs2, ArgsString2, mode);
    }

    @ApiOperation(value = "标签更换样式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tagId", value = "标签ID", required = true, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "styleId", value = "需更改的目标样式ID", required = true, dataType = "long", paramType = "query"),
    })
    @PutMapping("/tag/style")
    @Log("标签更换样式")
    public ResponseEntity<ResultBean> updateTagStyleById(@RequestParam long tagId, @RequestParam long styleId, @RequestParam Integer mode) {
        return tagService.updateTagStyleById(tagId, styleId, mode);
    }

    @ApiOperation(value = "对标签进行刷新或设置定期刷新", notes = "定期刷新才需加cron字段")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = "0为对标签刷新 1为对指定路由器的所有标签刷新 2对标签定期刷新 3对路由器定期刷新", dataType = "int", paramType = "query")
    })
    @PutMapping("/tag/flush")
    @Log("对指定属性的标签集合进行刷新")
    public ResponseEntity<ResultBean> tagFlush(@RequestBody @ApiParam("标签或路由器信息集合") RequestBean requestBean, @RequestParam Integer mode) {
        // 对指定的标签刷新
        if (mode.equals(ModeConstant.DO_BY_TAG)) {
            ResponseBean responseBean = tagService.flushTags(requestBean);
            return ResponseHelper.buildSuccessResultBean(responseBean);
        }
        // 对路由器下的所有标签刷新
        else if (mode.equals(ModeConstant.DO_BY_ROUTER)) {
            ResponseBean responseBean = tagService.flushTagsByRouter(requestBean);
            return new ResponseEntity<>(ResultBean.success(responseBean), HttpStatus.OK);
        } else if (mode.equals(ModeConstant.DO_BY_TAG_CYCLE) || mode.equals(ModeConstant.DO_BY_ROUTER_CYCLE)) {
            ResponseBean responseBean = tagService.flushTagsByCycle(requestBean, mode);
            return ResponseHelper.buildSuccessResultBean(responseBean);
        } else
            return ResponseHelper.buildBadRequestResultBean("参数有误 请正确选择mode");
    }

    @ApiOperation(value = "对标签进行巡检或设置定期巡检", notes = "定期巡检才需加cron字段")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = " 0为对标签巡检 1为对指定路由器的所有标签巡检 2对标签定期巡检 3对路由器定期巡检", dataType = "int", paramType = "query")
    })
    @PutMapping("/tag/scan")
    @Log("对标签进行巡检")
    public ResponseEntity<ResultBean> tagScan(@RequestBody @ApiParam("标签或路由器信息集合") RequestBean requestBean, @RequestParam Integer mode) {
        // 对指定的标签巡检
        if (mode.equals(ModeConstant.DO_BY_TAG)) {
            ResponseBean responseBean = tagService.scanTags(requestBean);
            return ResponseHelper.buildSuccessResultBean(responseBean);
        }
        // 对路由器下的所有标签巡检
        else if (mode.equals(ModeConstant.DO_BY_ROUTER)) {
            ResponseBean responseBean = tagService.scanTagsByRouter(requestBean);
            return ResponseHelper.buildSuccessResultBean(responseBean);
        } else if (mode.equals(ModeConstant.DO_BY_TAG_CYCLE) || mode.equals(ModeConstant.DO_BY_ROUTER_CYCLE)) {
            ResponseBean responseBean = tagService.scanTagsByCycle(requestBean, mode);
            return ResponseHelper.buildSuccessResultBean(responseBean);
        } else
            return ResponseHelper.buildBadRequestResultBean("参数有误 请正确选择mode");
    }

    @ApiOperation("对标签进行禁用或启用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = " 0禁用 1启用", dataType = "int", paramType = "query")
    })
    @PutMapping("/tag/status")
    @RequiresPermissions("切换状态")
    @Log("对标签进行禁用或启用")
    public ResponseEntity<ResultBean> changeStatus(@RequestBody @ApiParam("标签集合") RequestBean requestBean, @RequestParam @Min(message = "data.page.min", value = 0) @Max(message = "data.mode.max", value = 1) Integer mode) {
        return ResponseHelper.buildSuccessResultBean(tagService.changeStatus(requestBean, mode));
    }

    @ApiOperation("查看所有变价超时的标签信息")
    @GetMapping("/tags/overtime")
    @RequiresPermissions("查看所有变价超时的标签信息")
    public ResponseEntity<ResultBean> getOverTimeTags() {
        List<Tag> tagList = tagService.findBySql("SELECT * FROM tags WHERE  (completeTime IS NULL OR  execTime  IS NULL OR  completeTime =0 OR  execTime =0) AND waitUpdate = 0", Tag.class);
        ResponseEntity<ResultBean> result;
        if ((result = ResponseUtil.testListSize("没有相应的标签或商品 请重新选择", tagList)) != null) return result;
        List<TagVo> tagVos = CopyUtil.copyTag(tagList);
        return ResponseHelper.buildSuccessResultBean(tagVos);
    }

    @ApiOperation("对指定的标签闪灯或结束闪灯")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = " 0结束闪灯 1闪灯", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "typeMode", value = " 0对标签 1对路由器", dataType = "int", paramType = "query")
    })
    @PutMapping("/tag/light")
    @RequiresPermissions("标签闪灯")
    @Log("标签闪灯")
    public ResponseEntity<ResultBean> changeLightStatus(@RequestBody @ApiParam("标签或路由器信息集合") RequestBean requestBean, @RequestParam @Min(message = "data.page.min", value = 0) @Max(message = "data.mode.max", value = 1) Integer mode, @RequestParam Integer typeMode) {
        if (typeMode.equals(0))
            return ResponseHelper.buildSuccessResultBean(tagService.changeLightStatus(requestBean, mode));
        else if (typeMode.equals(1))
            return ResponseHelper.buildSuccessResultBean(tagService.changeLightStatusByRouter(requestBean, mode));
        else
            return ResponseHelper.buildBadRequestResultBean("参数有误");
    }

    @ApiOperation("对指定的标签发出标签移除命令")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = " 0对标签 1对路由器", dataType = "int", paramType = "query")
    })
    @PutMapping("/tag/remove")
    @Log("对指定的标签发出标签移除命令")
    public ResponseEntity<ResultBean> removeTagCommand(@RequestBody @ApiParam("标签集合") RequestBean requestBean, @RequestParam @Min(message = "data.page.min", value = 0) @Max(message = "data.mode.max", value = 1) Integer mode) {
        return ResponseHelper.buildSuccessResultBean(tagService.removeTagCommand(requestBean, mode));
    }

    @ApiOperation("获得标签可绑定的所有样式")
    @PostMapping("/tag/styles")
    @CrossOrigin(origins = "*", maxAge = 3600)
    public ResponseEntity<ResultBean> getStyles(@RequestBody @ApiParam("标签集合") RequestBean requestBean) {
        List<Tag> tags = RequestBeanUtil.getTagsByRequestBean(requestBean);
        List<Style> styles = styleService.findAll();
        List<Style> resultList = new ArrayList<>();
        for (Tag tag : tags)
            for (Style style : styles)
                if (TagAndRouterUtil.judgeTagMatchStyle(tag, style))
                    resultList.add(style);
        return ResponseHelper.buildSuccessResultBean(CopyUtil.copyStyle(resultList));
    }

    @ApiOperation("对所有标签进行巡检")
    @GetMapping("/tags/scan")
    @Log("对所有标签进行巡检")
    public ResponseEntity<ResultBean> scanAllTags() {
        return ResponseHelper.buildSuccessResultBean(tagService.scanAllTags());
    }

    @ApiOperation("对工作的标签进行批量改价操作")
    @PutMapping("/tags/test")
    @Log("对工作的标签进行批量改价操作")
    @ApiIgnore
    public ResponseEntity<ResultBean> testAllTags() {
        List<Tag> tags = tagService.findAll();
        for (Tag tag : tags) {
            Good good = tag.getGood();
            if (good != null) {
                good.setWaitUpdate(0);
                good.setRegionNames("name price promotePrice origin shelfNumber spec category");
                goodDao.save(good);
            }
            tag.setWaitUpdate(0);
        }
        SendCommandUtil.updateTagStyle(tags, true, false);
        return new ResponseEntity<>(ResultBean.success("成功"), HttpStatus.OK);

    }

    @ApiOperation("对标签进行改价")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = "0为对指定的标签改价 1对所有等待变价的标签改价 2对所有启用的标签改价", dataType = "int", paramType = "query")
    })
    @PostMapping("/tag/update")
    @Log("对标签进行改价")
    public ResponseEntity<ResultBean> tagUpdate(@RequestBody(required = false) @ApiParam("标签集合") RequestBean requestBean, @RequestParam Integer mode) {
        ResponseBean responseBean;
        if (mode == 0) {
            List<Tag> tags = RequestBeanUtil.getTagsByRequestBean(requestBean);
            responseBean = SendCommandUtil.updateTagStyle(tags, true, false);
        } else if (mode == 1) {
            List<Tag> tags = tagService.findBySql(SqlConstant.getQuerySql(TableConstant.TABLE_TAGS, ArrtributeConstant.GOOD_WAITUPDATE, "=", "0"), Tag.class);
            responseBean = SendCommandUtil.updateTagStyle(tags, true, false);
        } else {
            List<Tag> tags = tagService.findBySql(SqlConstant.getQuerySql(TableConstant.TABLE_TAGS, ArrtributeConstant.TAG_FORBIDSTATE, "=", "1"), Tag.class);
            responseBean = SendCommandUtil.updateTagStyle(tags, true, false);
        }
        return ResponseHelper.buildSuccessResultBean(responseBean);
    }

    @ApiOperation("测试rabbitmq")
    @PostMapping("/tag/testRabbitMq")
    @Log("测试rabbitmq")
    @ApiIgnore
    public ResponseEntity<ResultBean> testRabbitMq() {
        RabbiMqSendBean rabbiMqSendBean = new RabbiMqSendBean();
        List<Tag> tags = tagService.findAll();
        rabbiMqSendBean.setTags(tags);
        rabbiMqSendBean.setIsWaiting(true);
        //rabbitMqSender.send(rabbiMqSendBean);
        SendCommandUtil.updateTagStyle(rabbiMqSendBean.getTags(), rabbiMqSendBean.getIsWaiting(), false);
        return new ResponseEntity<>(ResultBean.success(rabbiMqSendBean), HttpStatus.OK);
    }

    @ApiOperation(value = "墨水瓶测试命令")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = " 0为对标签巡检 1为对指定路由器的所有标签巡检", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "type", value = " 对应测试命令1-5", dataType = "int", paramType = "query")
    })
    @PutMapping("/tag/testInkScreen")
    @Log("墨水瓶测试命令")
    public ResponseEntity<ResultBean> testInkScreen(@RequestBody RequestBean requestBean, @RequestParam Integer type, @RequestParam Integer mode) {
        ResponseBean responseBean = tagService.testInkScreen(requestBean, type, mode);
        return ResponseHelper.buildSuccessResultBean(responseBean);
    }
}
