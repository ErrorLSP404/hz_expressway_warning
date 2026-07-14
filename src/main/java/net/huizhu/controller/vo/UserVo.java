package net.huizhu.controller.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserVo {

    private Long id;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Long userNo;

    private String mobile;

    /**
     * 用户类型(0:超级管理员 1.部门管理员 2.科室管理员)
     */
    private Integer userType;

    /**
     * 名称
     */
    private String name;

    /**
     * 备注
     */
    private String remark;
}
