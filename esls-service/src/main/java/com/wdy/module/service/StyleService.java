package com.wdy.module.service;


import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.entity.Dispms;
import com.wdy.module.entity.Style;

import java.util.List;
import java.util.Optional;

public interface StyleService extends Service {
    List<Style> findAll();

    List<Style> findAll(Integer page, Integer count);

    Style saveOne(Style style);

    List<Style> saveOne(String styleType);

    List<Style> findByWidthOrderByStyleNumber(Integer width);

    Optional<Style> findById(Long id);

    boolean deleteById(Long id);

    ResponseBean deleteByStyleNumber(String styleNumber);

    // 刷新选用改样式的标签 或 定期刷新
    ResponseBean flushTags(RequestBean requestBean, Integer mode);

    ResponseBean newStyleById(long styleId, List<Dispms> dispms, Style style, Integer mode, Integer update);

    // 更新样式的小样式
    ResponseBean updateStyleById(long styleId, List<Long> dispmIds, Style style);

    // 根据样式number查找样式说明
    List<Style> findByStyleNumber(String styleNumber);

    Style findByStyleNumberAndIsPromote(String styleNumber, Byte isPromote);
}
