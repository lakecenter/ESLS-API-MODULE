package com.wdy.module.entity;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToIntegerConverter;
import com.wdy.module.converter.StringToLongConverter;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "cyclejobs", schema = "tags", catalog = "")
@Data
@ToString
public class CycleJob {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
    @ExcelField(title = "id", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @Column(name = "cron")
    @ExcelField(title = "cron", order = 2)
    private String cron;
    @Column(name = "args")
    @ExcelField(title = "args", order = 3)
    private String args;
    @Column(name = "mode")
    @ExcelField(title = "mode", order = 4, readConverter = StringToIntegerConverter.class)
    private Integer mode;
    @Column(name = "type")
    @ExcelField(title = "type", order = 5, readConverter = StringToIntegerConverter.class)
    private Integer type;
    @Column(name = "description")
    @ExcelField(title = "description", order = 6)
    private String description;
}
