package com.wdy.module.entity;

import com.fasterxml.jackson.annotation.*;
import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToLongConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Data
@NoArgsConstructor
@Table(name = "permission", schema = "tags", catalog = "")
public class Permission {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "idOrGenerate")
    @GenericGenerator(name = "idOrGenerate", strategy = "com.wdy.module.serviceUtil.IdOrGenerate")
    @ExcelField(title = "主键", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @Column(name = "name", length = 50)
    @ExcelField(title = "权限描述", order = 2)
    private String name;
    @Column(name = "url", length = 50)
    @ExcelField(title = "权限URL", order = 3)
    private String url;
    @ManyToMany(mappedBy = "permissions")
    @ToStringExclude
    @JsonIgnore
    private List<Role> roles;

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public Permission(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
