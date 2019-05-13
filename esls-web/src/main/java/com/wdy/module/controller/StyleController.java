package com.wdy.module.controller;

import com.wdy.module.common.constant.ArrtributeConstant;
import com.wdy.module.common.constant.TableConstant;
import com.wdy.module.common.exception.ResultEnum;
import com.wdy.module.common.exception.ServiceException;
import com.wdy.module.common.request.QueryAllBean;
import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.common.response.ResponseHelper;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.dao.TagDao;
import com.wdy.module.dto.StyleVo;
import com.wdy.module.entity.*;
import com.wdy.module.entity.Tag;
import com.wdy.module.aop.Log;
import com.wdy.module.service.*;
import com.wdy.module.serviceUtil.*;
import com.wdy.module.system.SystemVersionArgs;
import com.wdy.module.utils.*;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.*;

@RestController
@Api(description = "样式管理API")
@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
public class StyleController {
    @Autowired
    private StyleService styleService;
    @Autowired
    private DispmsService dispmsService;
    @Autowired
    private GoodService goodService;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private NettyUtil nettyUtil;

    @ApiOperation(value = "根据条件获取样式信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件 可为所有字段", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryString", value = "查询条件的字符串", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "数量", dataType = "int", paramType = "query")
    })
    @GetMapping("/styles")
    @RequiresPermissions("系统菜单")
    public ResponseEntity<ResultBean> getStyles(@RequestParam(required = false) String query, @RequestParam(required = false) String queryString, @Min(message = "data.page.min", value = 0) @RequestParam(required = false) Integer page, @Min(message = "data.count.min", value = 0) @RequestParam(required = false) Integer count) throws Exception {
        String result = ConditionUtil.judgeArgument(query, queryString, page, count);
        return styleService.getEntityList(QueryAllBean.builder().query(query).queryString(queryString).page(page).pagecount(count).result(result).serviceName("StyleService").build());
    }

    @ApiOperation(value = "获取指定ID的样式信息")
    @GetMapping("/style/{id}")
    @RequiresPermissions("获取指定ID的信息")
    public ResponseEntity<ResultBean> getStyleById(@PathVariable Long id) {
        Optional<Style> result = styleService.findById(id);
        return ResponseHelper.buildBooleanResultBean(CopyUtil.copyStyle(Arrays.asList(result.get())), "此ID样式不存在", result.isPresent());
    }

    @ApiOperation("获得指定ID的样式的所有小样式信息")
    @GetMapping("/style/dispms/{id}")
    public ResponseEntity<ResultBean> getDispmses(@PathVariable Long id) {
        List<Dispms> dispmses = dispmsService.findByArrtribute(TableConstant.TABLE_DISPMS, ArrtributeConstant.TAG_STYLEID, String.valueOf(id), Dispms.class);
        ResponseEntity<ResultBean> result;
        if ((result = ResponseUtil.testListSize("没有对应ID的小样式", dispmses)) != null) return result;
        String dispmsesSort = SystemVersionArgs.dispmsesSort;
        for (int i = 1; i < dispmses.size(); i++) {
            int last = dispmsesSort.indexOf(dispmses.get(i - 1).getSourceColumn());
            int now = dispmsesSort.indexOf(dispmses.get(i).getSourceColumn());
            if (now < last) {
                Dispms temp = dispmses.get(i);
                dispmses.set(i, dispmses.get(i - 1));
                dispmses.set(i - 1, temp);
            }
        }
        return ResponseHelper.buildSuccessResultBean(CopyUtil.copyDispms(dispmses));
    }

    @ApiOperation(value = "添加样式信息")
    @GetMapping("/style")
    @RequiresPermissions("添加或修改信息")
    public ResponseEntity<ResultBean> saveStyleByStyleType(@RequestParam String styleType) {
        List<Style> result = styleService.saveOne(styleType);
        return ResponseHelper.buildSuccessResultBean(result);
    }

    @ApiOperation(value = "获得促销或非促销的样式信息")
    @GetMapping("/style/promote")
    public ResponseEntity<ResultBean> getStyleByStyleNumberAndType(@RequestParam String styleNumber, @RequestParam Byte isPromote) {
        return ResponseHelper.buildSuccessResultBean(styleService.findByStyleNumberAndIsPromote(styleNumber, isPromote));
    }

    @ApiOperation(value = "添加或修改样式信息")
    @PostMapping("/style")
    @RequiresPermissions("添加或修改信息")
    public ResponseEntity<ResultBean> saveStyle(@RequestBody @ApiParam(value = "样式信息JSON格式") Style style) {
        return ResponseHelper.buildSuccessResultBean(styleService.saveOne(style));
    }

    @ApiOperation(value = "根据ID删除样式信息")
    @DeleteMapping("/style/{id}")
    @Log("根据ID删除样式信息")
    @RequiresPermissions("删除指定ID的信息")
    public ResponseEntity<ResultBean> deleteStyleById(@PathVariable Long id) {
        boolean flag = styleService.deleteById(id);
        return ResponseHelper.buildBooleanResultBean("删除成功", "删除失败！没有指定ID的样式", flag);
    }

    @ApiOperation(value = "根据styleNumber删除样式信息")
    @DeleteMapping("/style/styleNumber/{styleNumber}")
    @Log("根据styleNumber删除样式信息")
    @RequiresPermissions("删除指定ID的信息")
    public ResponseEntity<ResultBean> deleteStyleByStyleNumber(@PathVariable String styleNumber) {
        return ResponseHelper.buildSuccessResultBean(styleService.deleteByStyleNumber(styleNumber));
    }

    //    @ApiOperation(value = "更改指定ID样式的小样式")
//    @PostMapping("/style/update")
//    @Log("更改指定ID样式的小样式")
//    public ResponseEntity<ResultBean> updateStyleById(@RequestParam long styleId, @RequestBody @ApiParam(value = "样式信息Id List格式") List<Long> dispmIds) {
//        List<Style> styleList = dispmsService.findByArrtribute(TableConstant.TABLE_STYLE, ArrtributeConstant.TABLE_ID, String.valueOf(styleId), Style.class);
//        if(styleList==null || styleList.size()==0)
//            return new ResponseEntity<>(ResultBean.error("没有指定的样式,请先添加样式"), HttpStatus.BAD_REQUEST);
//        ResponseEntity<ResultBean> result ;
//        if ((result = ResponseUtil.testListSize("没有对应ID的样式", styleList)) != null) return result;
//        // 返回前端提示信息
//        return new ResponseEntity<>(ResultBean.success(styleService.updateStyleById(styleId,dispmIds,styleList.get(0))), HttpStatus.OK);
//    }
    @ApiOperation(value = "新建或修改样式同时绑定小样式")
    @PostMapping("/style/new")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = "0为添加 1为修改", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "update", value = "是否需要更新样式对应的标签(0不需要 1需要)", dataType = "int", paramType = "query"),
    })
    @RequiresPermissions("新建或修改样式同时绑定小样式")
    public ResponseEntity<ResultBean> newStyleById(@RequestParam long styleId, @RequestBody @ApiParam(value = "样式信息JSON格式") List<Dispms> dispms, @RequestParam Integer mode, @RequestParam Integer update) {
        Optional<Style> style = styleService.findById(styleId);
        return ResponseHelper.buildBooleanResultBean(styleService.newStyleById(styleId, dispms, style.get(), mode, update), "没有指定的样式,请先添加样式", style.isPresent());
    }

    @ApiOperation(value = "刷新选用该样式的标签或设置定期刷新", notes = "定期刷新才需加beginTime和cycleTime字段")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = " 0为刷新选用该样式的标签 1定期刷新", dataType = "int", paramType = "query")
    })
    @PutMapping("/style/flush")
    @Log("刷新选用该样式的标签或设置定期刷新")
    @RequiresPermissions("刷新选用该样式的标签或设置定期刷新")
    public ResponseEntity<ResultBean> flushTags(@RequestBody @ApiParam("样式集合") RequestBean requestBean, @RequestParam @Min(message = "data.page.min", value = 0) @Max(message = "data.mode.max", value = 1) Integer mode) {
        return ResponseHelper.buildSuccessResultBean(styleService.flushTags(requestBean, mode));
    }

    @ApiOperation("生成指定ID样式的所有小样式图片")
    @GetMapping("/style/photo/{id}")
    @RequiresPermissions("生成指定ID样式的所有小样式图片")
    public ResponseEntity<ResultBean> createStyle(@PathVariable long id, @RequestParam Long goodId) {
        Style style = styleService.findById(id).get();
        Good good = goodService.findById(goodId);
        Collection<Dispms> dispmses = style.getDispmses();
        for (Dispms dispms : dispmses) {
            ImageHelper.getRegionImage(dispms, style.getStyleNumber(), good);
        }
        return ResponseHelper.buildSuccessResultBean("成功");
    }

    @ApiOperation("生成指定ID样式的所有小样式图片")
    @GetMapping("/style/dism/photo/{id}")
    @RequiresPermissions("生成指定ID样式的所有小样式图片")
    public ResponseEntity<ResultBean> createDism(@PathVariable long id, @RequestParam Long goodId, @RequestParam String styleNumber) {
        Dispms dispms = dispmsService.findById(id).get();
        Good good = goodService.findById(goodId);
        ImageHelper.getRegionImage(dispms, styleNumber, good);
        return ResponseHelper.buildSuccessResultBean("成功");
    }

    @ApiOperation("向选用此样式的所有标签发送样式")
    @GetMapping("/style/sendDism/{id}")
    @Log("向选用此样式的标签发送样式")
    @RequiresPermissions("向选用此样式的所有标签发送样式")
    public ResponseEntity<ResultBean> sendDism(@PathVariable long id) {
        ResponseBean responseBean;
        List<Tag> tags = tagDao.findByStyleId(id);
        if (tags.size() > 1) {
            nettyUtil.awakeFirst(tags);
            responseBean = SendCommandUtil.updateTagStyle(tags, false, false);
            nettyUtil.awakeOverLast(tags);
        } else
            responseBean = SendCommandUtil.updateTagStyle(tags, false, false);
        return ResponseHelper.buildSuccessResultBean(responseBean);
    }
}
