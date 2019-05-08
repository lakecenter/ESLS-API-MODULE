package com.wdy.module.entity;

import com.fasterxml.jackson.annotation.*;
import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "goods", schema = "tags", catalog = "")
@Data
@Proxy(lazy = false)
public class Good implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "idOrGenerate")
    @GenericGenerator(name = "idOrGenerate", strategy = "com.wdy.module.serviceUtil.IdOrGenerate")
    @ExcelField(title = "主键", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @Column(name = "name")
    @ExcelField(title = "商品名字", order = 2)
    private String name;
    @Column(name = "origin")
    @ExcelField(title = "商品产地", order = 3)
    private String origin;
    @Column(name = "provider")
    @ExcelField(title = "商品提供商", order = 4)
    private String provider;
    @Column(name = "unit")
    @ExcelField(title = "商品单位", order = 5)
    private String unit;
    @Column(name = "barCode")
    @ExcelField(title = "条形码", order = 6)
    private String barCode;
    @Column(name = "qrCode")
    @ExcelField(title = "二维码", order = 7)
    private String qrCode;
    @Column(name = "operator")
    @ExcelField(title = "操作员", order = 8)
    private String operator;
    @Column(name = "importTime")
    @ExcelField(title = "商品导入时间", order = 9, readConverter = StringToTimestampConverter.class)
    private Timestamp importTime;
    @Column(name = "promotionReason")
    @ExcelField(title = "促销理由", order = 10)
    private String promotionReason;
    @Column(name = "price")
    @ExcelField(title = "商品原价格", order = 12)
    private String price;
    @Column(name = "promotePrice")
    @ExcelField(title = "商品促销价格", order = 13)
    private String promotePrice;
    @Column(name = "imageUrl")
    @ExcelField(title = "图片URL", order = 14)
    private String imageUrl;
    @Column(name = "waitUpdate")
    @ExcelField(title = "是否等待更新", order = 15, readConverter = StringToIntegerConverter.class)
    private Integer waitUpdate;
    @Column(name = "shelfNumber")
    @ExcelField(title = "货号", order = 16)
    private String shelfNumber;
    @Column(name = "spec")
    @ExcelField(title = "规格", order = 17)
    private String spec;
    @Column(name = "category")
    @ExcelField(title = "类别", order = 18)
    private String category;
    @Column(name = "rfu01")
    @ExcelField(title = "自定义字段1", order = 19)
    private String rfu01;
    @Column(name = "rfu02")
    @ExcelField(title = "自定义字段2", order = 20)
    private String rfu02;
    @Column(name = "rfus01")
    @ExcelField(title = "自定义字段3", order = 21)
    private String rfus01;
    @Column(name = "rfus02")
    @ExcelField(title = "自定义字段4", order = 22)
    private String rfus02;
    @Column(name = "regionNames")
    @ExcelField(title = "改价区域名集合", order = 23)
    private String regionNames;
    @Column(name = "stock")
    @ExcelField(title = "库存量", order = 24)
    private String stock;
    @Column(name = "isPromote")
    @ExcelField(title = "是否促销", order = 25, readConverter = StringToByteConverter.class)
    private Byte isPromote;
    @Column(name = "promoteTimeGap")
    @ExcelField(title = "促销时间起止时间", order = 26)
    private String promoteTimeGap;
    @OneToMany(mappedBy = "good", fetch = FetchType.EAGER)
    @JsonIgnore
    private Collection<Tag> tags;

    public Good() {
    }

    public Good(String barCode, String name, String price, String promotePrice, String promotionReason, String unit, String origin, String spec, String category, String shelfNumber, String rfus01, String rfus02, String qrCode, String provider) {
        this.barCode = barCode;
        this.name = name;
        this.price = price;
        this.promotePrice = promotePrice;
        this.promotionReason = promotionReason;
        this.unit = unit;
        this.origin = origin;
        this.spec = spec;
        this.category = category;
        this.shelfNumber = shelfNumber;
        this.rfus01 = rfus01;
        this.rfus02 = rfus02;
        this.qrCode = qrCode;
        this.provider = provider;
    }

    @Override
    public String toString() {
        return "Good{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", origin='" + origin + '\'' +
                ", provider='" + provider + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}
