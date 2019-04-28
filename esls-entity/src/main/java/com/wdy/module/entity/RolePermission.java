package com.wdy.module.entity;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToLongConverter;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "role_permission", schema = "tags", catalog = "")
@Data
@NoArgsConstructor
@ToString
public class RolePermission {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
    @ExcelField(title = "id", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @Column(name = "permissionId")
    @ExcelField(title = "permissionId", order = 1, readConverter = StringToLongConverter.class)
    private Long permissionId;
    @Column(name = "roleId")
    @ExcelField(title = "roleId", order = 1, readConverter = StringToLongConverter.class)
    private Long roleId;

    public RolePermission(Long permissionId, Long roleId) {
        this.permissionId = permissionId;
        this.roleId = roleId;
    }

}
