package com.wdy.module.entity;

import com.fasterxml.jackson.annotation.*;
import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToByteConverter;
import com.wdy.module.converter.StringToLongConverter;
import lombok.Data;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
    @ExcelField(title = "id", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @Column(name = "type")
    @ExcelField(title = "type", order = 2, readConverter = StringToByteConverter.class)
    private Byte type;
    @Column(name = "number")
    @ExcelField(title = "number", order = 3)
    private String number;
    @Column(name = "fatherShop")
    @ExcelField(title = "fatherShop", order = 4)
    private String fatherShop;
    @Column(name = "name")
    @ExcelField(title = "name", order = 5)
    private String name;
    @Column(name = "manager")
    @ExcelField(title = "manager", order = 6)
    private String manager;
    @Column(name = "address")
    @ExcelField(title = "address", order = 7)
    private String address;
    @Column(name = "account")
    @ExcelField(title = "account", order = 8)
    private String account;
    @Column(name = "password")
    @ExcelField(title = "password", order = 9)
    private String password;
    @Column(name = "phone")
    @ExcelField(title = "phone", order = 10)
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
                Objects.equals(password, shop.password) &&
                Objects.equals(phone, shop.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, number, fatherShop, name, manager, address, account, password, phone);
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
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
