package com.wdy.module.dao;

import com.wdy.module.entity.Style;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StyleDao extends JpaRepository<Style, Long> {
    List<Style> findByStyleNumber(String styleNumber);

    List<Style> findByWidthOrderByStyleNumber(Integer width);

    List<Style> findByWidthOrWidthOrderByStyleNumber(Integer width0, Integer width1);

    Style findByStyleNumberAndIsPromote(String styleNumber, Byte isPromote);
}
