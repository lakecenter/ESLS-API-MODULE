package com.wdy.module.entity;

import com.fasterxml.jackson.annotation.*;
import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.*;
import lombok.Data;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
    @ExcelField(title = "id", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @Column(name = "name")
    @ExcelField(title = "name", order = 2)
    private String name;
    @Column(name = "origin")
    @ExcelField(title = "origin", order = 3)
    private String origin;
    @Column(name = "provider")
    @ExcelField(title = "provider", order = 4)
    private String provider;
    @Column(name = "unit")
    @ExcelField(title = "unit", order = 5)
    private String unit;
    @Column(name = "barCode")
    @ExcelField(title = "barCode", order = 6)
    private String barCode;
    @Column(name = "qrCode")
    @ExcelField(title = "qrCode", order = 7)
    private String qrCode;
    @Column(name = "operator")
    @ExcelField(title = "operator", order = 8)
    private String operator;
    @Column(name = "importTime")
    @ExcelField(title = "importTime", order = 9, readConverter = StringToTimestampConverter.class)
    private Timestamp importTime;
    @Column(name = "promotionReason")
    @ExcelField(title = "promotionReason", order = 10)
    private String promotionReason;
    @Column(name = "status")
    @ExcelField(title = "status", order = 11, readConverter = StringToIntegerConverter.class)
    private Integer status;
    @Column(name = "price")
    @ExcelField(title = "price", order = 12)
    private String price;
    @Column(name = "promotePrice")
    @ExcelField(title = "promotePrice", order = 13)
    private String promotePrice;
    @Column(name = "imageUrl")
    @ExcelField(title = "imageUrl", order = 14)
    private String imageUrl;
    @Column(name = "waitUpdate")
    @ExcelField(title = "waitUpdate", order = 15, readConverter = StringToIntegerConverter.class)
    private Integer waitUpdate;
    @Column(name = "shelfNumber")
    @ExcelField(title = "shelfNumber", order = 16)
    private String shelfNumber;
    @Column(name = "spec")
    @ExcelField(title = "spec", order = 17)
    private String spec;
    @Column(name = "category")
    @ExcelField(title = "category", order = 18)
    private String category;
    @Column(name = "rfu01")
    @ExcelField(title = "rfu01", order = 19)
    private String rfu01;
    @Column(name = "rfu02")
    @ExcelField(title = "rfu02", order = 20)
    private String rfu02;
    @Column(name = "rfus01")
    @ExcelField(title = "rfus01", order = 21)
    private String rfus01;
    @Column(name = "rfus02")
    @ExcelField(title = "rfus02", order = 22)
    private String rfus02;
    @Column(name = "regionNames")
    @ExcelField(title = "regionNames", order = 23)
    private String regionNames;
    @Column(name = "stock")
    @ExcelField(title = "stock", order = 24)
    private String stock;
    @Column(name = "isPromote")
    @ExcelField(title = "isPromote", order = 25, readConverter = StringToByteConverter.class)
    private Byte isPromote;
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
