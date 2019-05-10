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
import com.wdy.module.serviceUtil.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
                List<Tag> tagList = findTagsByStyle(style);
                for (Tag tag : tagList)
                    if (tag.getGood() != null)
                        tags.add(tag);
            }
        }
        if (mode == 0) {
            log.info("向选用该样式的标签发送刷新命令");
            if (tags.size() > 1) {
                try {
                    nettyUtil.awakeFirst(tags);
                } catch (Exception e) {
                }
                try {
                    responseBean = SendCommandUtil.sendCommandWithTags(tags, contentType, CommandConstant.COMMANDTYPE_TAG);
                } catch (Exception e) {
                }
                try {
                    nettyUtil.awakeOverLast(tags);
                } catch (Exception e) {
                }
            } else
                responseBean = SendCommandUtil.sendCommandWithTags(tags, contentType, CommandConstant.COMMANDTYPE_TAG);
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
            dynamicTask.addTask(requestBean, ModeConstant.DO_BY_TAG_STYLE_FLUSH, 0);
            return new ResponseBean(requestBean.getItems().size(), requestBean.getItems().size());
        }
        return responseBean;
    }

    @Override
    @Transactional
    public ResponseBean newStyleById(long styleId, List<Dispms> dispms, Style style, Integer mode, Integer update) {
        int sum = dispms.size();
        int successnumber = 0;
        if (CollectionUtils.isEmpty(dispms))
            return new ResponseBean(sum, successnumber);
        if (dispms.get(0).getStyle() != null && !dispms.get(0).getStyle().getId().equals(style.getId()))
            return new ResponseBean(sum, successnumber);
        // 0添加
        if (mode == 0) {
            for (int i = 0; i < dispms.size(); i++) {
                try {
                    Dispms dispm = dispms.get(i);
                    if (dispm.getId() != 0)
                        continue;
                    dispm.setStyle(style);
                    dispmsDao.saveAndFlush(dispm);
                    refreshSession(dispm);
                    successnumber++;
                } catch (Exception e) {
                    System.out.println("StyleService - updateStyleById : " + e);
                }
            }
            return new ResponseBean(sum, successnumber);
        }
        // 1修改
        else {
            for (Dispms dispm : dispms) {
                try {
                    if (dispm.getId() == 0)
                        continue;
                    dispm.setStyle(style);
                    dispmsDao.save(dispm);
                    successnumber++;
                } catch (Exception e) {
                    System.out.println("StyleService - updateStyleById : " + e);
                }
            }
            if (update == 1) {
                List<Style> styleList = findByStyleNumber(style.getStyleNumber());
                List<Tag> resultTags = new ArrayList<>();
                for (Style s : styleList) {
                    List<Tag> tags = findByArrtribute(TableConstant.TABLE_TAGS, ArrtributeConstant.TAG_STYLEID, String.valueOf(s.getId()), Tag.class);
                    List<Tag> realTags = new ArrayList<>();
                    for (Tag tag : tags) {
                        if (tag.getGood() == null)
                            continue;
                        if (tag.getGood().getIsPromote() != null && style.getIsPromote() != null && tag.getGood().getIsPromote().equals(style.getIsPromote()))
                            realTags.add(tag);
                        else if (tag.getGood().getIsPromote() == null && (style.getIsPromote() == null || style.getIsPromote().equals(0)))
                            realTags.add(tag);
                        else if (style.getIsPromote() == null && (tag.getGood().getIsPromote() == null || tag.getGood().getIsPromote().equals(0)))
                            realTags.add(tag);
                    }
                    resultTags.addAll(realTags);
                }
                // 通过标签实体的路由器IP地址发送更改标签内容包
                SendCommandUtil.updateTagStyle(resultTags, false, false);
            }
            return new ResponseBean(sum, successnumber);
        }
    }

    // 样式更改
    @Override
    public ResponseBean updateStyleById(long styleId, List<Long> dispmIds, Style style) {
        int sum = dispmIds.size();
        int successnumber = 0;
        // 更新所有小样式
        for (Long id : dispmIds) {
            try {
                Dispms dispms = dispmsDao.findById(id).get();
                dispms.setStyle(style);
                dispmsDao.save(dispms);
                successnumber++;
            } catch (Exception e) {
                System.out.println("StyleService - updateStyleById : " + e);
            }
        }
        return new ResponseBean(sum, successnumber);
    }

    @Override
    public List<Style> findByStyleNumber(String styleNumber) {
        return styleDao.findByStyleNumber(styleNumber);
    }

    @Override
    public Style findByStyleNumberAndIsPromote(String styleNumber, Byte isPromote) {
        return styleDao.findByStyleNumberAndIsPromote(styleNumber, isPromote);
    }

    @Override
    public List<Style> findAll() {
        return styleDao.findAll();
    }

    @Override
    public List<Style> findAll(Integer page, Integer count) {
        List<Style> content = styleDao.findAll(PageRequest.of(page, count, Sort.Direction.DESC, "styleNumber")).getContent();
        return content;
    }

    @Override
    public Style saveOne(Style style) {
        return styleDao.save(style);
    }

    @Override
    public List<Style> findByWidthOrderByStyleNumber(Integer width) {
        return styleDao.findByWidthOrderByStyleNumber(width);
    }

    @Override
    public List<Style> saveOne(String styleType) {
        List<Style> returnResultList = new ArrayList<>();
        Style style = new Style();
        style.setStyleType(styleType);
        Integer width;
        String begin = "" + styleType.charAt(0) + styleType.charAt(2);
        // 250 122
        if (styleType.contains("2.1") && styleType.contains("黑白")) {
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
        List<Style> styles;
        if (width < 255) {
            styles = styleDao.findByWidthOrWidthOrderByStyleNumber(212, 250);
        } else {
            styles = findByWidthOrderByStyleNumber(width);
        }
        // 获取最大的样式编码
        String styleNumber = String.valueOf(Integer.valueOf(styles.get(styles.size() - 1).getStyleNumber()) + 1);
        for (int i = 1; i < styles.size(); i++) {
            Style last = styles.get(i - 1);
            Style s = styles.get(i);
            if (Integer.valueOf(s.getStyleNumber()) - Integer.valueOf(last.getStyleNumber()) > 1) {
                styleNumber = String.valueOf(Integer.valueOf(last.getStyleNumber()) + 1);
                break;
            }
        }
        style.setStyleNumber(styleNumber);
        style.setIsPromote((byte) 0);
        Style style1 = new Style();
        BeanUtils.copyProperties(style, style1);
        style1.setStyleType(style.getStyleType() + "-促销");
        style1.setIsPromote((byte) 1);
        Style result = styleDao.save(style);
        Style result1 = styleDao.save(style1);
        returnResultList.add(result);
        returnResultList.add(result1);
        return returnResultList;
    }

    @Override
    public Optional<Style> findById(Long id) {
        return styleDao.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        Optional<Style> style = findById(id);
        if (!style.isPresent()) return false;
        List<Tag> tagList = findTagsByStyle(style.get());
        if (!CollectionUtils.isEmpty(tagList)) {
            TagUtil.setBaseTagStyle(tagList);
        }
        if ("2101 2102 2901 2902 4201 4202".contains(style.get().getStyleNumber()))
            return false;
        List<Dispms> dispmsIds = dispmsDao.findByStyleId(id);
        for (Dispms dispms : dispmsIds)
            dispmsDao.deleteById(dispms.getId());
        try {
            styleDao.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ResponseBean deleteByStyleNumber(String styleNumber) {
        int sum = 0, successNumber = 0;
        ResponseBean responseBean = new ResponseBean(sum, successNumber);
        List<Style> styles = findByStyleNumber(styleNumber);
        if (CollectionUtils.isEmpty(styles))
            return responseBean;
        List<Tag> tagList = findTagsByStyle(styles.get(0));
        if (!CollectionUtils.isEmpty(tagList)) {
            TagUtil.setBaseTagStyle(tagList);
        }
        sum = styles.size();
        for (Style s : styles) {
            boolean b = deleteById(s.getId());
            if (b)
                successNumber++;
        }
        return new ResponseBean(sum, successNumber);
    }

    private List<Tag> findTagsByStyle(Style style) {
        List<Style> styleList = findByStyleNumber(style.getStyleNumber());
        List<Tag> resultTags = new ArrayList<>();
        for (Style s : styleList) {
            List<Tag> tagList = findByArrtribute(TableConstant.TABLE_TAGS, ArrtributeConstant.TAG_STYLEID, String.valueOf(s.getId()), Tag.class);
            resultTags.addAll(tagList);
        }
        return resultTags;
    }

    @Autowired
    private StyleDao styleDao;
    @Autowired
    private DispmsDao dispmsDao;
    @Autowired
    private NettyUtil nettyUtil;
    @Autowired
    private CycleJobDao cycleJobDao;
    @Autowired
    private DynamicTask dynamicTask;
}
