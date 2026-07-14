package net.huizhu.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
* <p>
    * 
    * </p>
*
* @author huizhu
* @since 2022-05-09
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public class WorkImage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单id
     */
    private Long id;

    /**
            * 工单编号
            */
    private Long workNo;

            /**
            * 工单图片
            */
    private String workImage;


}
