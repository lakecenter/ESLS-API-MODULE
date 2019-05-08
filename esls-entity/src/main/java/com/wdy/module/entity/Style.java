package com.wdy.module.entity;

import com.fasterxml.jackson.annotation.*;
import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.*;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "styles", schema = "tags", catalog = "")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Style implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @ExcelField(title = "主键", order = 1, readConverter = StringToLongConverter.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "idOrGenerate")
    @GenericGenerator(name = "idOrGenerate", strategy = "com.wdy.module.serviceUtil.IdOrGenerate")
    private Long id;
    @Column(name = "styleNumber")
    @ExcelField(title = "样式编码", order = 2)
    private String styleNumber;
    @Column(name = "styleType")
    @ExcelField(title = "样式名字", order = 3)
    private String styleType;
    @Column(name = "width")
    @ExcelField(title = "宽度", order = 4, readConverter = StringToIntegerConverter.class)
    private Integer width;
    @Column(name = "height")
    @ExcelField(title = "高度", order = 5, readConverter = StringToIntegerConverter.class)
    private Integer height;
    @Column(name = "isPromote")
    @ExcelField(title = "是否促销样式", order = 6, readConverter = StringToByteConverter.class)
    private Byte isPromote;
    @OneToMany(mappedBy = "style")
    @JsonIgnore
    @ToStringExclude
    private Collection<Dispms> dispmses;
    @OneToMany(mappedBy = "style")
    @JsonIgnore
    @ToStringExclude
    private Collection<Tag> tags;

    @Override
    public String toString() {
        return "Style{" +
                "id=" + id +
                ", styleNumber='" + styleNumber + '\'' +
                ", styleType='" + styleType + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", isPromote=" + isPromote +
                '}';
    }
}