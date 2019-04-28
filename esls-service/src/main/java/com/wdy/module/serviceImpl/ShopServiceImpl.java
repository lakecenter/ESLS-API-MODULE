package com.wdy.module.serviceImpl;

import com.wdy.module.common.constant.ModeConstant;
import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.cycleJob.DynamicTask;
import com.wdy.module.dao.CycleJobDao;
import com.wdy.module.dao.ShopDao;
import com.wdy.module.entity.CycleJob;
import com.wdy.module.entity.Shop;
import com.wdy.module.service.ShopService;
import com.wdy.module.serviceUtil.RequestBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("ShopService")
public class ShopServiceImpl extends BaseServiceImpl implements ShopService {
    @Autowired
    private ShopDao shopDao;
    @Autowired
    private CycleJobDao cycleJobDao;
    @Autowired
    private DynamicTask dynamicTask;
    @Override
    public List<Shop> findAll() {
        return shopDao.findAll();
    }

    @Override
    public List<Shop> findAll(Integer page, Integer count) {
        List<Shop> content = shopDao.findAll(PageRequest.of(page, count, Sort.Direction.DESC, "id")).getContent();
        return content;
    }

    @Override
    public Shop saveOne(Shop shop) {
        return shopDao.save(shop);
    }

    @Override
    public Optional<Shop> findById(Long id) {
        return shopDao.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        try{
            shopDao.deleteById(id);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
    // 定期刷新
    @Override
    public ResponseBean tagsByCycle(RequestBean requestBean, Integer mode) {
        // 设置定期刷新
        CycleJob cyclejob = new CycleJob();
        cyclejob.setCron("无效字段");
        if(mode==0)
            cyclejob.setDescription("对商店下所有标签定期刷新");
        else
            cyclejob.setDescription("对商店下所有标签定期巡检");
        cyclejob.setArgs(RequestBeanUtil.getRequestBeanAsString(requestBean));
        cyclejob.setMode(mode);
        cyclejob.setType(ModeConstant.DO_BY_SHOP);
        cycleJobDao.save(cyclejob);
        dynamicTask.addTask(requestBean,ModeConstant.DO_BY_SHOP,mode);
        return new ResponseBean(requestBean.getItems().size(), requestBean.getItems().size());
    }
}
