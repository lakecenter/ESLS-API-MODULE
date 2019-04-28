package com.wdy.module.serviceImpl;

import com.wdy.module.dao.DispmsDao;
import com.wdy.module.entity.Dispms;
import com.wdy.module.service.DispmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("DispmsService")
public class DispmsServiceImpl extends BaseServiceImpl  implements DispmsService {
    @Autowired
    private DispmsDao dispmsDao;
    @Override
    public List<Dispms> findAll() {
        return dispmsDao.findAll();
    }

    @Override
    public List<Dispms> findAll(Integer page, Integer count) {
        List<Dispms> content = dispmsDao.findAll(PageRequest.of(page, count, Sort.Direction.DESC, "id")).getContent();
        return content;
    }
    @Override
    public Dispms saveOne(Dispms dispms) {
//        Style style = dispms.getStyle();
//        if(dispms.getId()!=0 && style!=null) {
//            Long styleId = style.getId();
//            // 通过styleId查找使用了此样式的所有标签实体
//            try {
//                List<Tag> tags = findByArrtribute(TableConstant.TABLE_TAGS, ArrtributeConstant.TAG_STYLEID, String.valueOf(styleId), com.datagroup.ESLS.com.wdy.module.entity.Tag.class);
//                // 通过标签实体的路由器IP地址发送更改标签内容包
//                SendCommandUtil.updateTagStyle(tags);
//            }
//            catch (Exception e){
//                System.out.println("DispmsServiceImpl - saveOne : "+e);
//            }
//        }
        return dispmsDao.save(dispms);
    }

    @Override
    public Optional<Dispms> findById(Long id) {
        return dispmsDao.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        try{
            dispmsDao.deleteById(id);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    @Override
    public Dispms findByStyleIdAndColumnTypeAndSourceColumn(Long styleId, String columnType, String sourceColumn) {
        return dispmsDao.findByStyleIdAndColumnTypeAndSourceColumn(styleId,columnType,sourceColumn);
    }
}
