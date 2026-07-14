package net.huizhu.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
    * 工单日志表
    * </p>
*
* @author huizhu
* @since 2022-04-28
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public class WorkOrderLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单记录Id
     */
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
            * 工单单号
            */
    private Long workNo;

            /**
            * 0.不是最新节点  1.是最新节点
            */
    private Integer lastNode;

            /**
            * 节点
            */
    private Integer node;

            /**
            * 报警类型 0:无事件发生 1:交通事件2:路面异物3:公路标志标线损坏4:安防设施损坏5:非法占用公路行为
            */
    private Integer type;

            /**
            * 部门名称
            */
    private String dName;

    /**
     * 历程桩号
     */
    private String cameraLocation;


}
