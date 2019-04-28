package com.wdy.module.entity;

import com.fasterxml.jackson.annotation.*;
import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringExclude;

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
public class Style implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
    @ExcelField(title = "id", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @Column(name = "styleNumber")
    @ExcelField(title = "styleNumber", order = 2)
    private String styleNumber;
    @Column(name = "styleType")
    @ExcelField(title = "styleType", order = 3)
    private String styleType;
    @Column(name = "width")
    @ExcelField(title = "width", order = 4, readConverter = StringToIntegerConverter.class)
    private Integer width;
    @Column(name = "height")
    @ExcelField(title = "height", order = 5, readConverter = StringToIntegerConverter.class)
    private Integer height;
    @Column(name = "isPromote")
    @ExcelField(title = "isPromote", order = 6, readConverter = StringToByteConverter.class)
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