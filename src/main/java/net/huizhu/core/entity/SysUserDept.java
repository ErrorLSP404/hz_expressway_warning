package net.huizhu.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
* <p>
    * 用户部门中间表
    * </p>
*
* @author huizhu
* @since 2022-04-28
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public class SysUserDept implements Serializable {

    private static final long serialVersionUID = 1L;

            /**
            * 用户id
            */
    private Long uId;

            /**
            * 部门id
            */
    private Long dId;

            /**
            * 部门名称
            */
    private String dName;


}
