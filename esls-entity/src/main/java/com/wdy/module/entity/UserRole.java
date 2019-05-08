package com.wdy.module.entity;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToLongConverter;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "user_role", schema = "tags", catalog = "")
@Data
@NoArgsConstructor
@ToString
public class UserRole {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "idOrGenerate")
    @GenericGenerator(name = "idOrGenerate", strategy = "com.wdy.module.serviceUtil.IdOrGenerate")
    @ExcelField(title = "主键", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @Column(name = "roleId")
    @ExcelField(title = "角色ID", order = 2, readConverter = StringToLongConverter.class)
    private Long roleId;
    @Column(name = "userId")
    @ExcelField(title = "用户ID", order = 3, readConverter = StringToLongConverter.class)
    private Long userId;

    public UserRole(Long roleId, Long userId) {
        this.roleId = roleId;
        this.userId = userId;
    }
}
