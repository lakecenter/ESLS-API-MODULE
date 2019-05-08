package com.wdy.module.entity;

import com.fasterxml.jackson.annotation.*;
import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToByteConverter;
import com.wdy.module.converter.StringToLongConverter;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "shops", schema = "tags", catalog = "")
@Data
public class Shop implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "idOrGenerate")
    @GenericGenerator(name = "idOrGenerate", strategy = "com.wdy.module.serviceUtil.IdOrGenerate")
    @ExcelField(title = "主键", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @Column(name = "type")
    @ExcelField(title = "店铺类型", order = 2, readConverter = StringToByteConverter.class)
    private Byte type;
    @Column(name = "number")
    @ExcelField(title = "店铺编号", order = 3)
    private String number;
    @Column(name = "fatherShop")
    @ExcelField(title = "父店铺", order = 4)
    private String fatherShop;
    @Column(name = "name")
    @ExcelField(title = "店铺名字", order = 5)
    private String name;
    @Column(name = "manager")
    @ExcelField(title = "店铺管理员", order = 6)
    private String manager;
    @Column(name = "address")
    @ExcelField(title = "店铺地址", order = 7)
    private String address;
    @Column(name = "account")
    @ExcelField(title = "店铺描述", order = 8)
    private String account;
    @Column(name = "phone")
    @ExcelField(title = "店铺联系方式", order = 9)
    private String phone;
    @OneToMany(mappedBy = "shop")
    private Collection<Router> routers;
    @OneToMany(mappedBy = "shop")
    @JsonIgnore
    private Collection<User> users;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shop shop = (Shop) o;
        return id == shop.id &&
                type == shop.type &&
                Objects.equals(number, shop.number) &&
                Objects.equals(fatherShop, shop.fatherShop) &&
                Objects.equals(name, shop.name) &&
                Objects.equals(manager, shop.manager) &&
                Objects.equals(address, shop.address) &&
                Objects.equals(account, shop.account) &&
                Objects.equals(phone, shop.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, number, fatherShop, name, manager, address, account, phone);
    }

    @Override
    public String toString() {
        return "Shop{" +
                "id=" + id +
                ", type=" + type +
                ", number='" + number + '\'' +
                ", fatherShop='" + fatherShop + '\'' +
                ", name='" + name + '\'' +
                ", manager='" + manager + '\'' +
                ", address='" + address + '\'' +
                ", account='" + account + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
