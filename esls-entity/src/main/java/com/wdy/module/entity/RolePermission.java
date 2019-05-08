package com.wdy.module.entity;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToLongConverter;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "role_permission", schema = "tags", catalog = "")
@Data
@NoArgsConstructor
@ToString
public class RolePermission {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "idOrGenerate")
    @GenericGenerator(name = "idOrGenerate", strategy = "com.wdy.module.serviceUtil.IdOrGenerate")
    @ExcelField(title = "主键", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @Column(name = "permissionId")
    @ExcelField(title = "权限ID", order = 1, readConverter = StringToLongConverter.class)
    private Long permissionId;
    @Column(name = "roleId")
    @ExcelField(title = "角色ID", order = 1, readConverter = StringToLongConverter.class)
    private Long roleId;

    public RolePermission(Long permissionId, Long roleId) {
        this.permissionId = permissionId;
        this.roleId = roleId;
    }

}
