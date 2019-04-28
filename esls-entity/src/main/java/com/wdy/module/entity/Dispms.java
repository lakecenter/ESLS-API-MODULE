package com.wdy.module.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@ToString
@Data
@Table(name = "dispms", schema = "tags", catalog = "")
public class Dispms implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "x")
    private Integer x;
    @Column(name = "y")
    private Integer y;
    @Column(name = "width")
    private Integer width;
    @Column(name = "height")
    private Integer height;
    @Column(name = "sourceColumn")
    private String sourceColumn;
    @Column(name = "columnType")
    private String columnType;
    @Column(name = "backgroundColor")
    private Integer backgroundColor;
    @Column(name = "text")
    private String text;
    @Column(name = "startText")
    private String startText;
    @Column(name = "endText")
    private String endText;
    @Column(name = "fontType")
    private String fontType;
    @Column(name = "fontFamily")
    private String fontFamily;
    @Column(name = "fontSize")
    private Integer fontSize;
    @Column(name = "fontColor")
    private Integer fontColor;
    @Column(name = "status")
    private Byte status;
    @Column(name = "imageUrl")
    private String imageUrl;
    @Column(name = "backup")
    private String backup;
    @Column(name = "regionId")
    private String regionId;
    @ManyToOne
    @JoinColumn(name = "styleid", referencedColumnName = "id")
    @JsonIgnore
    private Style style;
    public Dispms() {
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, x, y, width, height, sourceColumn, columnType, backgroundColor, text, startText, endText, fontType, fontFamily, fontSize, fontColor, status, imageUrl, backup, regionId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dispms dispms = (Dispms) o;
        return id == dispms.id &&
                Objects.equals(name, dispms.name) &&
                Objects.equals(x, dispms.x) &&
                Objects.equals(y, dispms.y) &&
                Objects.equals(width, dispms.width) &&
                Objects.equals(height, dispms.height) &&
                Objects.equals(sourceColumn, dispms.sourceColumn) &&
                Objects.equals(columnType, dispms.columnType) &&
                Objects.equals(backgroundColor, dispms.backgroundColor) &&
                Objects.equals(text, dispms.text) &&
                Objects.equals(startText, dispms.startText) &&
                Objects.equals(endText, dispms.endText) &&
                Objects.equals(fontType, dispms.fontType) &&
                Objects.equals(fontFamily, dispms.fontFamily) &&
                Objects.equals(fontSize, dispms.fontSize) &&
                Objects.equals(fontColor, dispms.fontColor) &&
                Objects.equals(status, dispms.status) &&
                Objects.equals(imageUrl, dispms.imageUrl) &&
                Objects.equals(backup, dispms.backup) &&
                Objects.equals(regionId, dispms.regionId);
    }
}
