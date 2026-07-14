package net.huizhu.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
* <p>
    * 用户科室中间表
    * </p>
*
* @author huizhu
* @since 2022-04-28
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public class SysUserOff implements Serializable {

    private static final long serialVersionUID = 1L;

            /**
            * 用户id
            */
    private Long uId;

            /**
            * 科室id
            */
    private Long oId;

            /**
            * 科室名称
            */
    private String oName;


}
