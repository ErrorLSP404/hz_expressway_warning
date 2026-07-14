package net.huizhu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {

    ADMIN(1L,"超级管理员"),USER(2L,"普通用户"),DEPT(3L,"部门管理员"),OFFICE(4L,"科室管理员");

    private Long code;
    private String desc;
}
