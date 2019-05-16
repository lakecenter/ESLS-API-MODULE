package com.wdy.module.controller;

import com.wdy.module.common.constant.ArrtributeConstant;
import com.wdy.module.common.constant.TableConstant;
import com.wdy.module.common.request.QueryAllBean;
import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.request.RequestItem;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.common.response.ResponseHelper;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.dto.RouterVo;
import com.wdy.module.entity.Router;
import com.wdy.module.entity.Shop;
import com.wdy.module.aop.Log;
import com.wdy.module.netty.command.CommandConstant;
import com.wdy.module.service.*;
import com.wdy.module.serviceUtil.*;
import com.wdy.module.utils.*;
import com.wdy.module.entity.Tag;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import java.io.*;
import java.util.*;

@RestController
@Api(description = "路由器管理API")
@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
public class RouterController {

    @Autowired
    private RouterService routerService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private TagService tagService;

    @ApiOperation(value = "根据条件获取路由器信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件 可为所有字段", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryString", value = "查询条件的字符串", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "数量", dataType = "int", paramType = "query")
    })
    @GetMapping("/routers")
    @RequiresPermissions("系统菜单")
    public ResponseEntity<ResultBean> getRouter(@RequestParam(required = false) String query, @RequestParam(required = false) String queryString, @Min(message = "data.page.min", value = 0) @RequestParam(required = false) Integer page, @Min(message = "data.count.min", value = 0) @RequestParam(required = false) Integer count) throws Exception {
        String result = ConditionUtil.judgeArgument(query, queryString, page, count);
        return routerService.getEntityList(QueryAllBean.builder().query(query).queryString(queryString).page(page).pagecount(count).result(result).serviceName("RouterService").build());
    }

    @ApiOperation(value = "获取指定ID的路由器信息")
    @GetMapping("/router/{id}")
    @RequiresPermissions("获取指定ID的信息")
    public ResponseEntity<ResultBean> getRouterById(@PathVariable Long id) {
        Optional<Router> result = routerService.findById(id);
        return ResponseHelper.BooleanResultBean(CopyUtil.copyRouter(Arrays.asList(result.get())), "此ID路由器不存在", result.isPresent());
    }

    @ApiOperation(value = "添加或修改路由器信息(路由器设置)")
    @PostMapping("/router")
    @RequiresPermissions("添加或修改信息")
    public ResponseEntity<ResultBean> saveRouter(@RequestBody @ApiParam(value = "路由器信息json格式") RouterVo routerVo) {
        Router router = new Router();
        BeanUtils.copyProperties(routerVo, router);
        // 绑定商店
        if (routerVo.getShopId() != 0) {
            Shop shop = new Shop();
            if (!shopService.findById(routerVo.getShopId()).isPresent())
                return ResponseHelper.BadRequest("商店不存在");
            shop.setId(routerVo.getShopId());
            router.setShop(shop);
        }
        List result = new ArrayList();
        result.add(routerService.saveOne(router));
        return ResponseHelper.OK(CopyUtil.copyRouter(result));
    }

    @ApiOperation(value = "根据ID删除路由器信息")
    @DeleteMapping("/router/{id}")
    @Log("根据ID删除路由器信息")
    @RequiresPermissions("删除指定ID的信息")
    public ResponseEntity<ResultBean> deleteRouterById(@PathVariable Long id) {
        boolean flag = routerService.deleteById(id);
        if (flag) {
            List<Tag> tags = tagService.findByArrtribute(TableConstant.TABLE_TAGS, ArrtributeConstant.TAG_ROUTERID, String.valueOf(id), Tag.class);
            for (Tag tag : tags) {
                tag.setRouter(null);
                tagService.save(tag);
            }
        }
        return ResponseHelper.BooleanResultBean("删除成功", "删除失败！没有指定ID的路由器或者此ID下的路由器仍有绑定的标签", flag);
    }

    @ApiOperation("根据多个字段搜索数据")
    @PostMapping("/routers/search")
    @RequiresPermissions("查询和搜索功能")
    public ResponseEntity<ResultBean> searchRoutersByConditon(@RequestParam String connection, @Min(message = "data.page.min", value = 0) @RequestParam Integer page, @RequestParam @Min(message = "data.count.min", value = 0) Integer count, @RequestBody @ApiParam(value = "查询条件json格式") RequestBean requestBean) {
        List<Router> routerList = routerService.findAllBySql(TableConstant.TABLE_ROUTERS, connection, requestBean, page, count, Router.class);
        return ResponseHelper.OK(CopyUtil.copyRouter(routerList));
    }

    // 更换路由器
    @ApiOperation("根据指定属性更换路由器")
    @PutMapping("/router/change")
    @Log("更换路由器")
    @RequiresPermissions("更换路由器")
    public ResponseEntity<ResultBean> changeRouter(@RequestParam @ApiParam("源字段名") String sourceQuery, @RequestParam @ApiParam("源字段值") String sourceQueryString, @RequestParam @ApiParam("目的字段名") String targetQuery, @RequestParam @ApiParam("目的字段值") String targetQueryString) {
        ResponseBean responseBean = routerService.changeRouter(sourceQuery, sourceQueryString, targetQuery, targetQueryString);
        return ResponseHelper.OK(responseBean);
    }

    // 路由器巡检（查询路由器信息）
    @ApiOperation(value = "路由器巡检", notes = "定期巡检才需加cron表达式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = " 0为指定路由器巡检 1定期巡检", dataType = "int", paramType = "query")
    })
    @PutMapping("/router/scan")
    @RequiresPermissions("路由器巡检")
    @Log("路由器巡检")
    public ResponseEntity<ResultBean> routerScan(@RequestBody @ApiParam("路由器信息集合") RequestBean requestBean, @RequestParam Integer mode) {
        ResponseBean responseBean;
        if (mode == 0)
            responseBean = routerService.routerScan(requestBean);
        else
            responseBean = routerService.routerScanByCycle(requestBean);
        return ResponseHelper.OK(responseBean);
    }

    @ApiOperation("对所有路由器发起巡检")
    @PutMapping("/routers/scan")
    @RequiresPermissions("对所有路由器发起巡检")
    @Log("对所有路由器发起巡检")
    public ResponseEntity<ResultBean> routersScan() {
        ResponseBean responseBean = routerService.routersScan();
        return ResponseHelper.OK(responseBean);
    }

    // 路由器设置
    @ApiOperation("发送路由器设置命令")
    @PutMapping("/router/setting")
    @RequiresPermissions("发送路由器设置命令")
    @Log("发送路由器设置命令")
    public ResponseEntity<ResultBean> routerSetting(@RequestBody @ApiParam("路由器信息集合") RequestBean requestBean) {
        ResponseBean responseBean = routerService.settingRouter(requestBean);
        return ResponseHelper.OK(responseBean);
    }

    // 路由器移除
    @ApiOperation("发送路由器移除命令")
    @PutMapping("/router/remove")
    @RequiresPermissions("发送路由器移除命令")
    public ResponseEntity<ResultBean> routerRemove(@RequestBody @ApiParam("路由器信息集合") RequestBean requestBean) {
        ResponseBean responseBean = routerService.routerRemove(requestBean);
        return ResponseHelper.OK(responseBean);
    }

    // AP信息写入
    @ApiOperation("AP测试命令")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = " 0AP信息写入(3者) 1AP信息读取 2AP发送无线帧（channelId） 3AP停止发送无线帧 4AP接收无线帧（channelId） 5AP停止接收无线帧 6获取接收无线帧RSSI", dataType = "int", paramType = "query")
    })
    @PutMapping("/router/test")
    @RequiresPermissions("AP测试")
    @Log("AP测试命令")
    public ResponseEntity<ResultBean> routerTest(@RequestBody @ApiParam("路由器信息集合") RequestBean requestBean, @RequestParam(required = false) String barCode, @RequestParam(required = false) String channelId, @RequestParam(required = false) String hardVersion, @RequestParam Integer mode) {
        // 9获取接收无线帧RSSI
        if (mode == 6) {
            Map<String, String> map = SocketChannelHelper.rssiResponse;
            return ResponseHelper.OK(map);
        }
        List<Router> routerList = new ArrayList<>();
        for (RequestItem items : requestBean.getItems()) {
            routerList.addAll(routerService.findByArrtribute(TableConstant.TABLE_ROUTERS, items.getQuery(), items.getQueryString(), Router.class));
        }
        ResponseBean responseBean = new ResponseBean(0, 0);
        // 0AP信息写入
        if (mode == 0) {
            responseBean = SendCommandUtil.sendAPWrite(routerList, barCode, channelId, hardVersion);
        }
        // 1AP信息读取
        else if (mode == 1) {
            responseBean = SendCommandUtil.sendCommandWithRouters(routerList, CommandConstant.APREAD, CommandConstant.COMMANDTYPE_ROUTER);
        }
        // 2AP发送无线帧
        else if (mode == 2) {
            SocketChannelHelper.initRssiMap();
            responseBean = SendCommandUtil.sendAPByChannelId(routerList, channelId);
        }
        // 3AP停止发送无线帧
        else if (mode == 3) {
            responseBean = SendCommandUtil.sendCommandWithRouters(routerList, CommandConstant.APBYCHANNELIDSTOP, CommandConstant.COMMANDTYPE_ROUTER);
        }
        // 4AP接收无线帧
        else if (mode == 4) {
            responseBean = SendCommandUtil.sendAPReceiveByChannelId(routerList, channelId);
        }
        // 5AP停止接收无线帧
        else if (mode == 5) {
            responseBean = SendCommandUtil.sendCommandWithRouters(routerList, CommandConstant.APRECEIVEBYCHANNELIDSTOP, CommandConstant.COMMANDTYPE_ROUTER);
        }
        return ResponseHelper.OK(responseBean);
    }

    @ApiOperation("设置远程服务器信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = "0设置目标服务器IP 1删除当前历史连接IP记录 2发送查询历史连接IP列表 3接收查询历史连接IP列表信息", dataType = "int", paramType = "query")
    })
    @PutMapping("/router/remote")
    @RequiresPermissions("设置远程服务器信息")
    @Log("设置远程服务器信息")
    public ResponseEntity<ResultBean> settingRemote(@RequestBody @ApiParam("路由器信息集合") RequestBean requestBean, @RequestParam Integer mode, @RequestParam(required = false) String outNetIp) {
        // 3接收查询历史连接IP列表信息
        if (mode == 3) {
            Set<String> ipHistory = SocketChannelHelper.ipHistory;
            return ResponseHelper.OK(ipHistory);
        }
        List<Router> routerList = new ArrayList<>();
        for (RequestItem items : requestBean.getItems()) {
            routerList.addAll(routerService.findByArrtribute(TableConstant.TABLE_ROUTERS, items.getQuery(), items.getQueryString(), Router.class));
        }
        ResponseBean responseBean = new ResponseBean(0, 0);
        if (mode == 0) {
            responseBean = SendCommandUtil.setLocalhostIp(routerList, outNetIp);
        } else if (mode == 1) {
            responseBean = SendCommandUtil.sendCommandWithRouters(routerList, CommandConstant.DELETEIPRECORD, CommandConstant.COMMANDTYPE_ROUTER);
        } else if (mode == 2) {
            responseBean = SendCommandUtil.sendCommandWithRouters(routerList, CommandConstant.GETIPRECORD, CommandConstant.COMMANDTYPE_ROUTER);
        }
        return ResponseHelper.OK(responseBean);
    }

    @ApiOperation("上传路由器升级数据文件")
    @PutMapping("/router/upload")
    @RequiresPermissions("上传路由器升级数据文件")
    @Log("上传路由器升级数据文件")
    public ResponseEntity<ResultBean> uploadRouter(@ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile file, @RequestParam Long routerId) throws IOException {
        Router router = routerService.findById(routerId).get();
        ResponseBean responseBean = SendCommandUtil.sendCommandWithRoutersUpdate(Arrays.asList(router), file);
        return ResponseHelper.OK(responseBean);
    }
}
