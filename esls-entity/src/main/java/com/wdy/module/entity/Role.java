package com.wdy.module.entity;

import com.fasterxml.jackson.annotation.*;
import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToLongConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "role", schema = "tags", catalog = "")
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "idOrGenerate")
    @GenericGenerator(name = "idOrGenerate", strategy = "com.wdy.module.serviceUtil.IdOrGenerate")
    @ExcelField(title = "主键", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @Column(name = "type")
    @ExcelField(title = "角色类型", order = 2)
    private String type;
    @Column(name = "name")
    @ExcelField(title = "角色描述", order = 3)
    private String name;
    //角色 -- 权限关系：多对多关系;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permission", joinColumns = {@JoinColumn(name = "roleId")}, inverseJoinColumns = {@JoinColumn(name = "permissionId")})
    private List<Permission> permissions;
    // 用户 - 角色关系定义;
    // 一个角色对应多个用户
    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "roleId")}, inverseJoinColumns = {@JoinColumn(name = "userId")})
    @JsonIgnore
    private List<User> users;

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public Role(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
