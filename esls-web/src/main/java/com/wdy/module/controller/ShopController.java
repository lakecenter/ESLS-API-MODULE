package com.wdy.module.controller;

import com.wdy.module.common.constant.ArrtributeConstant;
import com.wdy.module.common.constant.TableConstant;
import com.wdy.module.common.request.QueryAllBean;
import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.response.ResponseHelper;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.dto.ShopVo;
import com.wdy.module.entity.*;
import com.wdy.module.aop.Log;
import com.wdy.module.service.*;
import com.wdy.module.serviceUtil.RequestBeanUtil;
import com.wdy.module.utils.ConditionUtil;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.*;

@RestController
@Api(description = "店铺管理API")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ShopController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private RouterService routerService;
    @Autowired
    private UserService userService;
    @Autowired
    private GoodService goodService;

    @ApiOperation(value = "根据条件获取店铺信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件 可为所有字段 ", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryString", value = "查询条件的字符串", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "count", value = "数量", dataType = "int", paramType = "query")
    })
    @GetMapping("/shops")
    @RequiresPermissions("系统菜单")
    public ResponseEntity<ResultBean> getShops(@RequestParam(required = false) String query, @RequestParam(required = false) String queryString, @Min(message = "data.page.min", value = 0) @RequestParam(required = false) Integer page, @Min(message = "data.count.min", value = 0) @RequestParam(required = false) Integer count) throws Exception {
        String result = ConditionUtil.judgeArgument(query, queryString, page, count);
        return shopService.getEntityList(QueryAllBean.builder().query(query).queryString(queryString).page(page).pagecount(count).result(result).serviceName("ShopService").build());
    }

    @ApiOperation(value = "获取指定ID的店铺信息")
    @GetMapping("/shop/{id}")
    @RequiresPermissions("获取指定ID的信息")
    public ResponseEntity<ResultBean> getShopById(@PathVariable Long id) {
        Optional<Shop> result = shopService.findById(id);
        return ResponseHelper.buildBooleanResultBean(result, "此ID店铺不存在", result.isPresent());
    }

    @ApiOperation(value = "获取某个店铺下的所有商品")
    @GetMapping("/shop/goods")
    @RequiresPermissions("系统菜单")
    public ResponseEntity<ResultBean> getGoodsByShop(@RequestBody @ApiParam(value = "店铺信息json格式") RequestBean requestBean) {
        List<Shop> shops = RequestBeanUtil.getShopsByRequestBean(requestBean);
        Map<String, List> shopToGoods = new HashMap<>();
        for (Shop shop : shops) {
            List<Good> goods = goodService.findByShopNumber(shop.getNumber());
            shopToGoods.put(shop.getNumber(), goods);
        }
        return ResponseHelper.buildSuccessResultBean(shopToGoods);
    }

    @ApiOperation(value = "添加或修改店铺信息")
    @PostMapping("/shop")
    @RequiresPermissions("添加或修改信息")
    public ResponseEntity<ResultBean> saveShop(@RequestBody @ApiParam(value = "店铺信息json格式") ShopVo shopVo) {
        Shop shop = new Shop();
        BeanUtils.copyProperties(shopVo, shop);
        List<Long> routers = shopVo.getRouterIds();
        for (int i = 0; routers != null && i < routers.size(); i++) {
            Optional<Router> router = routerService.findById(routers.get(i));
            if (router.isPresent()) {
                router.get().setShop(shop);
                routerService.saveOne(router.get());
            }
        }
        List<Long> users = shopVo.getUserIds();
        for (int i = 0; users != null && i < users.size(); i++) {
            User user = userService.findById(users.get(i));
            if (user != null) {
                user.setShop(shop);
                userService.saveOne(user);
            }
        }
        Shop reuslt = shopService.saveOne(shop);
        return ResponseHelper.buildSuccessResultBean(shopService.saveOne(reuslt));
    }

    @ApiOperation(value = "根据ID删除店铺信息")
    @DeleteMapping("/shop/{id}")
    @Log("根据ID删除店铺信息")
    @RequiresPermissions("删除指定ID的信息")
    public ResponseEntity<ResultBean> deleteShopById(@PathVariable Long id) {
        boolean flag = shopService.deleteById(id);
        if (flag) {
            List<Router> routers = routerService.findByArrtribute(TableConstant.TABLE_ROUTERS, ArrtributeConstant.SHOPID, String.valueOf(id), Router.class);
            for (Router r : routers) {
                r.setShop(null);
                routerService.saveOne(r);
            }
            List<User> users = userService.findByArrtribute(TableConstant.TABLE_USER, ArrtributeConstant.SHOPID, String.valueOf(id), User.class);
            for (User u : users) {
                u.setShop(null);
                userService.saveOne(u);
            }
        }
        return ResponseHelper.buildBooleanResultBean("删除成功", "删除失败！没有指定ID的店铺", flag);
    }

    @ApiOperation(value = "对标签进行巡检或设置定期巡检", notes = "定期巡检才需加cron字段")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = "0为商店下所有标签定期刷新 1为商店下所有标签定期巡检", dataType = "int", paramType = "query")
    })
    @PutMapping("/shop/cyclejob")
    @Log("设置商店定期任务")
    public ResponseEntity<ResultBean> shopCycelJob(@RequestBody @ApiParam("商店信息集合") RequestBean requestBean, @RequestParam Integer mode) {
        return ResponseHelper.buildSuccessResultBean(shopService.tagsByCycle(requestBean, mode));
    }
}
