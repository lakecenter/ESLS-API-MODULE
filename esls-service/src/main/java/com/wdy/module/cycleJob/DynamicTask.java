package com.wdy.module.cycleJob;

import com.wdy.module.common.constant.ModeConstant;
import com.wdy.module.common.constant.SqlConstant;
import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.request.RequestItem;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.dao.CycleJobDao;
import com.wdy.module.entity.*;
import com.wdy.module.serviceUtil.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import com.wdy.module.service.*;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Component("DynamicTask")
@Slf4j
public class DynamicTask {
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Autowired
    private TagService tagService;
    @Autowired
    private StyleService styleService;
    @Autowired
    private RouterService routerService;
    @Autowired
    private CycleJobDao cycleJobDao;
    private ScheduledFuture<?> future;
    private List<ScheduledFuture<?>> futerList = new ArrayList();

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    public void addFlushTask(String cron, RequestBean requestBean, Integer mode) {
        future = threadPoolTaskScheduler.schedule(new FlushTask(requestBean, mode), new CronTrigger(cron));
        futerList.add(future);
    }

    public void addStyleFlushTask(String cron, RequestBean requestBean) {
        future = threadPoolTaskScheduler.schedule(new StyleFlushTask(requestBean), new CronTrigger(cron));
        futerList.add(future);
    }

    public void addShopTask(String cron, RequestBean requestBean, Integer mode) {
        future = threadPoolTaskScheduler.schedule(new ShopTask(requestBean, mode), new CronTrigger(cron));
        futerList.add(future);
    }

    public void addTagScanTask(String cron, RequestBean requestBean, Integer mode) {
        future = threadPoolTaskScheduler.schedule(new TagScanTask(requestBean, mode), new CronTrigger(cron));
        futerList.add(future);
    }

    public void addRouterScanTask(String cron, RequestBean requestBean) {
        future = threadPoolTaskScheduler.schedule(new RouterScanTask(requestBean), new CronTrigger(cron));
        futerList.add(future);
    }

    public void addBaseGoodsScanTask(String cron, String filePath) {
        future = threadPoolTaskScheduler.schedule(new BaseGoodScanTask(filePath), new CronTrigger(cron));
        futerList.add(future);
    }

    public void addChangeGoodsScanTask(String cron, String filePath) {
        future = threadPoolTaskScheduler.schedule(new ChangeGoodScanTask(filePath), new CronTrigger(cron));
        futerList.add(future);
    }

    private class FlushTask implements Runnable {
        private RequestBean requestBean;
        private Integer mode;

        public FlushTask(RequestBean requestBean, Integer mode) {
            this.requestBean = requestBean;
            this.mode = mode;
        }

        @Override
        public void run() {
            if (mode.equals(ModeConstant.DO_BY_TAG_CYCLE)) {
                System.out.println("对标签定期刷新:" + new Date());
                tagService.flushTags(requestBean);
            } else if (mode.equals(ModeConstant.DO_BY_ROUTER_CYCLE)) {
                System.out.println("对路由器下的标签定期刷新:" + new Date());
                tagService.flushTagsByRouter(requestBean);
            }
        }
    }

    private class StyleFlushTask implements Runnable {
        private RequestBean requestBean;

        public StyleFlushTask(RequestBean requestBean) {
            this.requestBean = requestBean;
        }

        @Override
        public void run() {
            styleService.flushTags(requestBean, 0);
        }
    }

    private class ShopTask implements Runnable {
        private RequestBean requestBean;
        private Integer mode;

        public ShopTask(RequestBean requestBean, Integer mode) {
            this.requestBean = requestBean;
            this.mode = mode;
        }

        @Override
        public void run() {
            if (mode.equals(ModeConstant.DO_BY_TAG)) {
                System.out.println("对商店下所有标签定期刷新:" + new Date());
                List<Shop> shops = RequestBeanUtil.getShopsByRequestBean(requestBean);
                for (Shop shop : shops) {
                    List<Router> routers = (List) shop.getRouters();
                    for (Router r : routers) {
                        RequestBean req = new RequestBean();
                        req.getItems().add(new RequestItem("id", String.valueOf(r.getId())));
                        tagService.flushTagsByRouter(req);
                    }
                }
                tagService.flushTags(requestBean);
            } else if (mode.equals(ModeConstant.DO_BY_ROUTER)) {
                System.out.println("对商店下所有标签定期巡检:" + new Date());
                List<Shop> shops = RequestBeanUtil.getShopsByRequestBean(requestBean);
                for (Shop shop : shops) {
                    List<Router> routers = (List) shop.getRouters();
                    for (Router r : routers) {
                        RequestBean req = new RequestBean();
                        req.getItems().add(new RequestItem("id", String.valueOf(r.getId())));
                        tagService.scanTagsByRouter(req);
                    }
                }
            }
        }
    }

    private class TagScanTask implements Runnable {
        private RequestBean requestBean;
        private Integer mode;

        public TagScanTask(RequestBean requestBean, Integer mode) {
            this.requestBean = requestBean;
            this.mode = mode;
        }

        @Override
        public void run() {
            if (mode.equals(ModeConstant.DO_BY_TAG_CYCLE)) {
                System.out.println("对指定的标签定期巡检:" + new Date());
                tagService.scanTags(requestBean);
            } else if (mode.equals(ModeConstant.DO_BY_ROUTER_CYCLE)) {
                System.out.println("对指定的路由器的所有标签定期巡检:" + new Date());
                tagService.scanTagsByRouter(requestBean);
            }
        }
    }

    private class RouterScanTask implements Runnable {
        private RequestBean requestBean;

        public RouterScanTask(RequestBean requestBean) {
            this.requestBean = requestBean;
        }

        @Override
        public void run() {
            System.out.println("对指定的路由器定期巡检:" + new Date());
            routerService.routerScan(requestBean);
        }
    }

    private class BaseGoodScanTask implements Runnable {
        private String filePath;

        public BaseGoodScanTask(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void run() {
            Service service = (Service) SpringContextUtil.getBean("BaseService");
            List dataColumnList = service.findBySql(SqlConstant.QUERY_TABLIE_COLUMN + "\'" + "goods" + "\'");
            System.out.println("扫描指定目录下的商品基本文件  " + filePath + " " + new Date());
            File file = new File(filePath);
            File[] files = file.listFiles();
            try {
                for (int i = 0; i < files.length; i++) {
                    try {
                        // 添加
                        PoiUtil.importCsvDataFile(new FileInputStream(files[i]), dataColumnList, "goods", 0);
                        String startPath = filePath + File.separator + files[i].getName();
                        String endPath = filePath + "_finish" + File.separator + files[i].getName();
                        File startFile = new File(startPath);
                        FileUtils.copyFile(startFile, new File(endPath));
                        System.gc();
                        FileUtils.forceDelete(startFile);
                    } catch (Exception e) {
                        System.out.println(files[i].getName() + "导入失败  -   " + e);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ChangeGoodScanTask implements Runnable {
        private String filePath;

        public ChangeGoodScanTask(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void run() {
            System.out.println("扫描指定目录下的商品变价文件" + filePath + " " + new Date());
            GoodService goodService = (GoodService) SpringContextUtil.getBean("GoodService");
            File file = new File(filePath);
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                Service service = (Service) SpringContextUtil.getBean("BaseService");
                List dataColumnList = service.findBySql(SqlConstant.QUERY_TABLIE_COLUMN + "\'" + "goods" + "\'");
                try {
                    System.out.println(files[i].getName() + "开始导入数据库");
                    try {
                        // 修改
                        PoiUtil.importCsvDataFile(new FileInputStream(files[i]), dataColumnList, "goods", 1);
                    } catch (Exception e) {
                        System.out.println(files[i].getName() + "导入失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 开始变价
            ResponseBean responseBean = goodService.updateGoods(true);
            if (responseBean != null && responseBean.getSuccessNumber() == responseBean.getSum() && responseBean.getSum() != 0) {
                for (int i = 0; i < files.length; i++) {
                    try {
                        String startPath = filePath + File.separator + files[i].getName();
                        String endPath = filePath + "_finish" + File.separator + files[i].getName();
                        File startFile = new File(startPath);
                        FileUtils.copyFile(startFile, new File(endPath));
                        System.gc();
                        FileUtils.forceDelete(startFile);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public void init() {
        List<CycleJob> CycleJobs = cycleJobDao.findAll();
        for (CycleJob job : CycleJobs) {
            if (job.getState() == null || job.getState() == 0)
                continue;
            // 扫描商品基本文件  7
            if (job.getType().equals(ModeConstant.DO_BY_BASEGOODS_SCAN)) {
                addBaseGoodsScanTask(job.getCron(), job.getArgs());
                continue;
            }
            // 扫描商品变价文件 8
            else if (job.getType().equals(ModeConstant.DO_BY_CHANGEGOODS_SCAN)) {
                addChangeGoodsScanTask(job.getCron(), job.getArgs());
                continue;
            }
            RequestBean requestBean = RequestBeanUtil.stringtoRequestBean(job.getArgs());
            addTask(requestBean, job.getType(), job.getMode());
        }
        log.info("初始化定时任务成功");
    }

    public void addTask(RequestBean requestBean, Integer type, Integer mode) {
        for (RequestItem item : requestBean.getItems()) {
            RequestBean requestBeanItem = new RequestBean();
            requestBeanItem.getItems().add(item);
            // 标签刷新（0对标签 1对路由器）
            if (type.equals(ModeConstant.DO_BY_TAG_FLUSH))
                addFlushTask(item.getCron(), requestBeanItem, mode);
                // 标签巡检（0对标签 1对路由器）
            else if (type.equals(ModeConstant.DO_BY_TAG_SCAN)) {
                addTagScanTask(item.getCron(), requestBeanItem, mode);
            }
            // 路由器巡检
            else if (type.equals(ModeConstant.DO_BY_ROUTER_SCAN))
                addRouterScanTask(item.getCron(), requestBeanItem);
                // 对指定样式定期刷新
            else if (type.equals(ModeConstant.DO_BY_TAG_STYLE_FLUSH))
                addStyleFlushTask(item.getCron(), requestBeanItem);
            else if (type.equals(ModeConstant.DO_BY_SHOP))
                addShopTask(item.getCron(), requestBeanItem, mode);
        }
    }

    public void restartFutures() {
        for (ScheduledFuture<?> future : futerList)
            future.cancel(true);
        init();
    }
}
