package com.wdy.module.service;


import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.entity.Router;

import java.util.List;
import java.util.Optional;

public interface RouterService extends Service {
    List<Router> findAll();

    List<Router> findAll(Integer page, Integer count);

    Router saveOne(Router router);

    Optional<Router> findById(Long id);

    boolean deleteById(Long id);

    Router findByIp(String ip);

    Router findByOutNetIpAndPort(String OutNetIp, Integer port);

    Router findByBarCode(String barCode);

    // 更换路由器
    ResponseBean changeRouter(String sourceQuery, String sourceQueryString, String targetQuery, String targetQueryString);

    // 对路由器进行巡检
    ResponseBean routerScan(RequestBean requestBean);

    ResponseBean routersScan();

    ResponseBean routerScanByCycle(RequestBean requestBean);

    ResponseBean settingRouter(RequestBean requestBean);

    ResponseBean routerRemove(RequestBean requestBean);
}
