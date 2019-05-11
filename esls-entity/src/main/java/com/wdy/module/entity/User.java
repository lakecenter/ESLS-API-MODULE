package com.wdy.module.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "t_user", schema = "tags", catalog = "")
@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class User implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "idOrGenerate")
    @GenericGenerator(name = "idOrGenerate", strategy = "com.wdy.module.serviceUtil.IdOrGenerate")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "passwd")
    private String passwd;
    @Column(name = "rawPasswd")
    private String rawPasswd;
    @Column(name = "telephone")
    private String telephone;
    @Column(name = "address")
    private String address;
    @Column(name = "department")
    private String department;
    @Column(name = "createTime")
    private Timestamp createTime;
    @Column(name = "lastLoginTime")
    private Timestamp lastLoginTime;
    @Column(name = "status")
    private Byte status;
    @Column(name = "activateStatus")
    private Byte activateStatus;
    @Column(name = "mail")
    private String mail;
    @Column(name = "avatarUrl")
    private String avatarUrl;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "shopid", referencedColumnName = "id")
    private Shop shop;
    @ToStringExclude
    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "userId")}, inverseJoinColumns = {@JoinColumn(name = "roleId")})
    private List<Role> roleList;

    public User(String userName) {
        this.name = userName;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", passwd='" + passwd + '\'' +
                ", telephone='" + telephone + '\'' +
                ", address='" + address + '\'' +
                ", department='" + department + '\'' +
                ", createTime=" + createTime +
                ", lastLoginTime=" + lastLoginTime +
                ", roleList=" + roleList +
                '}';
    }
}



