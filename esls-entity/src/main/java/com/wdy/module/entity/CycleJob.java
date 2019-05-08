package com.wdy.module.entity;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToByteConverter;
import com.wdy.module.converter.StringToIntegerConverter;
import com.wdy.module.converter.StringToLongConverter;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "cyclejobs", schema = "tags", catalog = "")
@Data
@ToString
public class CycleJob {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "idOrGenerate")
    @GenericGenerator(name = "idOrGenerate", strategy = "com.wdy.module.serviceUtil.IdOrGenerate")
    @ExcelField(title = "主键", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @Column(name = "cron")
    @ExcelField(title = "Cron表达式", order = 2)
    private String cron;
    @Column(name = "args")
    @ExcelField(title = "参数", order = 3)
    private String args;
    @Column(name = "mode")
    @ExcelField(title = "模式", order = 4, readConverter = StringToIntegerConverter.class)
    private Integer mode;
    @Column(name = "type")
    @ExcelField(title = "类型", order = 5, readConverter = StringToIntegerConverter.class)
    private Integer type;
    @Column(name = "description")
    @ExcelField(title = "描述", order = 6)
    private String description;
    @Column(name = "state")
    @ExcelField(title = "禁用状态", order = 7, readConverter = StringToByteConverter.class)
    private Byte state;
}
