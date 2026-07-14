package net.huizhu.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
    * 科室表
    * </p>
*
* @author huizhu
* @since 2022-04-28
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public class Office implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
            /**
            * 创建时间
            */
    private LocalDateTime gmtCreate;

            /**
            * 修改时间
            */
    private LocalDateTime gmtModified;

            /**
            * 部门id
            */
    private Long dId;

            /**
            * 科室名称
            */
    private String name;


}
