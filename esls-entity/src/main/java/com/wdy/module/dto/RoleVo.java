package com.wdy.module.dto;

import lombok.Data;

import java.util.List;

/**
 * @program: esls-parent
 * @description:
 * @author: dongyang_wu
 * @create: 2019-05-11 23:17
 */
@Data
public class RoleVo {
    private Long id;
    private String type;
    private String name;
    private List<PermissionVo> permissions;
}