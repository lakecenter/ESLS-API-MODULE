package com.wdy.module.serviceImpl;

import com.wdy.module.common.constant.*;
import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.request.RequestItem;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.cycleJob.DynamicTask;
import com.wdy.module.dao.*;
import com.wdy.module.entity.*;
import com.wdy.module.netty.command.CommandConstant;
import com.wdy.module.service.StyleService;
import com.wdy.module.service.TagService;
import com.wdy.module.serviceUtil.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("StyleService")
@Slf4j
public class StyleServiceImpl extends BaseServiceImpl implements StyleService {
    // 向选用该样式的标签发送刷新命令
    @Override
    public ResponseBean flushTags(RequestBean requestBean, Integer mode) {
        String contentType = CommandConstant.FLUSH;
        ResponseBean responseBean = null;
        List<Tag> tags = new ArrayList<>();
        for (RequestItem items : requestBean.getItems()) {
            // 获取指定属性的所有标签
            List<Style> styleList = findByArrtribute(TableConstant.TABLE_STYLE, items.getQuery(), items.getQueryString(), Style.class);
            for (Style style : styleList) {
                List<Tag> tagList = findByArrtribute(TableConstant.TABLE_TAGS, ArrtributeConstant.TAG_STYLEID, String.valueOf(style.getId()), Tag.class);
                for(Tag tag:tagList)
                    if(tag.getGood()!=null)
                        tags.add(tag);
            }
        }
        if (mode == 0) {
            log.info("向选用该样式的标签发送刷新命令");
            if(tags.size()>1) {
                nettyUtil.awakeFirst(tags);
                responseBean = SendCommandUtil.sendCommandWithTags(tags, contentType, CommandConstant.COMMANDTYPE_TAG);
                nettyUtil.awakeOverLast(tags);
            }
            else
                responseBean = SendCommandUtil.sendCommandWithTags(tags,contentType,CommandConstant.COMMANDTYPE_TAG);
        } else if (mode == 1) {
            log.info("向选用该样式的标签发送定期刷新");
            // 设置定期刷新
            CycleJob cyclejob = new CycleJob();
            cyclejob.setCron("无效字段");
            cyclejob.setDescription("对选用指定样式的所有标签定期刷新");
            cyclejob.setArgs(RequestBeanUtil.getRequestBeanAsString(requestBean));
            cyclejob.setMode(0);
            cyclejob.setType(ModeConstant.DO_BY_TAG_STYLE_FLUSH);
            cycleJobDao.save(cyclejob);
            dynamicTask.addTask(requestBean,ModeConstant.DO_BY_TAG_STYLE_FLUSH,0);
            return new ResponseBean(requestBean.getItems().size(), requestBean.getItems().size());
        }
        return responseBean;
    }

    @Override
    public ResponseBean newStyleById(long styleId, List<Dispms> dispms, Style style, Integer mode, Integer update) {
        int sum = dispms.size();
        int successnumber = 0;
        // 0添加
        if(mode == 0) {
            for (Dispms dispm : dispms) {
                try {
                    if(dispm.getId()!=0)
                        continue;
                    dispm.setStyle(style);
                    dispmsDao.save(dispm);
                    successnumber++;
                } catch (Exception e) {
                    System.out.println("StyleService - updateStyleById : " + e);
                }
            }
            return new ResponseBean(sum,successnumber);
        }
        // 1修改
        else{
            for (Dispms dispm : dispms) {
                try {
                    if(dispm.getId()==0)
                        continue;
                    dispm.setStyle(style);
                    dispmsDao.save(dispm);
                    successnumber++;
                } catch (Exception e) {
                    System.out.println("StyleService - updateStyleById : " + e);
                }
            }
            if(update==1) {
                // 获取更改的区域
                List<Tag> tags = findByArrtribute(TableConstant.TABLE_TAGS, ArrtributeConstant.TAG_STYLEID, String.valueOf(styleId), Tag.class);
                // 通过标签实体的路由器IP地址发送更改标签内容包
                SendCommandUtil.updateTagStyle(tags,false,false);
            }
            return new ResponseBean(sum,successnumber);
        }
    }
    // 样式更改
    @Override
    public ResponseBean updateStyleById(long styleId, List<Long> dispmIds,Style style) {
        int sum = dispmIds.size();
        int successnumber = 0;
        // 更新所有小样式
        for(Long id : dispmIds) {
            try {
                Dispms dispms = dispmsDao.findById(id).get();
                dispms.setStyle(style);
                dispmsDao.save(dispms);
                successnumber++;
            } catch (Exception e) {
                System.out.println("StyleService - updateStyleById : " + e);
            }
        }
        return new ResponseBean(sum,successnumber);
    }

    @Override
    public Style findByStyleNumber(String styleNumber) {
        return styleDao.findByStyleNumber(styleNumber);
    }
    @Override
    public List<Style> findAll() {
        return styleDao.findAll();
    }
    @Override
    public List<Style> findAll(Integer page, Integer count){
        List<Style> content = styleDao.findAll(PageRequest.of(page, count, Sort.Direction.DESC, "id")).getContent();
        return content;
    }
    @Override
    public Style saveOne(Style style) {
        return styleDao.save(style);
    }

    @Override
    public List<Style> findByWidth(Integer width) {
        return styleDao.findByWidth(width);
    }

    @Override
    public Style saveOne(String styleType) {
        Style style = new Style();
        style.setStyleType(styleType);
        Integer width;
        String begin = ""+styleType.charAt(0)+styleType.charAt(2);
        // 250 122
        if(styleType.contains("2.1")  && styleType.contains("黑白")){
            begin = "21";
            String[] s = StyleType.keyToWHMap.get("25").split(" ");
            width = Integer.valueOf(s[0]);
            style.setWidth(width);
            style.setHeight(Integer.valueOf(s[1]));
        }
        // 212 104
        else {
            String[] s = StyleType.keyToWHMap.get(begin).split(" ");
            width = Integer.valueOf(s[0]);
            style.setWidth(width);
            style.setHeight(Integer.valueOf(s[1]));
        }
        List<Style> styles = new ArrayList<>();
        if(width<255){
            List<Style> stylesByWidth212 = findByWidth(212);
            styles.addAll(stylesByWidth212);
            List<Style> stylesByWidth250 = findByWidth(250);
            styles.addAll(stylesByWidth250);
        }
        else {
            styles = findByWidth(width);
        }
        // 获取最大的样式编码
        Integer maxStyleNumber = 0;
        for(Style s:styles){
            Integer styleNumber = Integer.valueOf(s.getStyleNumber());
            if(styleNumber>maxStyleNumber)
                maxStyleNumber = styleNumber;
        }
        String s = String.valueOf(maxStyleNumber);
        int size = Integer.valueOf(s.substring(2,s.length()));
        System.out.println(size);
        String end = String.valueOf(size+1);
        if(size+1<10)
            end = "0"+end;
        style.setStyleNumber(begin+end);
        return styleDao.save(style);
    }

    @Override
    public Optional<Style> findById(Long id) {
        return styleDao.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        List<Dispms> dispmsIds = dispmsDao.findByStyleId(id);
        for(Dispms dispms : dispmsIds)
            dispmsDao.deleteById(dispms.getId());
        try{
            styleDao.deleteById(id);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
    @Autowired
    private StyleDao styleDao;
    @Autowired
    private DispmsDao dispmsDao;
    @Autowired
    private NettyUtil nettyUtil;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private TagService tagService;
    @Autowired
    private CycleJobDao cycleJobDao;
    @Autowired
    private DynamicTask dynamicTask;
}
