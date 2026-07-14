package net.huizhu.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
    * 工单表
    * </p>
*
* @author huizhu
* @since 2022-04-28
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public class WorkOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单Id
     */
    private Long id;

    /**
    * 报警类型 0:无事件发生 1:交通事件2:路面异物3:公路标志标线损坏4:安防设施损坏5:非法占用公路行为
    */
    private Integer type;

    /**
    * 创建时间
    */
    private LocalDateTime gmtCreate;

    /**
    * 修改时间
    */
    private LocalDateTime gmtModified;

    /**
    * 路线编号
    */
    private String highwayName;

    /**
    * 里程庄号
    */
    private String cameraLocation;

    /**
    * 0.未派单  1.处理中  2.已办结
    */
    private Integer status;

    /**
    * 0.未超时 1.已超时
    */
    private Integer timeOut;

    /**
    * 时限
    */
    private LocalDateTime timePeriod;

    /**
    * 部门id
    */
    private Long dId;

    /**
    * 部门名称
    */
    private String dName;

    /**
    * 科室id
    */
    private Long oId;

    /**
    * 科室名称
    */
    private String oName;

    /**
    * 0.未撤销 1.已撤销
    */
    private Integer revocation;

    /**
    * 备注
    */
    private String remark;

    /**
     * 报警id
     */
    private Long aId;

    /**
     * 工单单号
     */
    private Long workNo;

    /**
     * 时限Int类型
     */
    private String timeOutStr;

    /**
     * 下单时间
     */
    private LocalDateTime orderTime;

    /**
     * 路段Id
     */
    private Long sectionId;

    /**
     * 高速ID
     */
    private Long highwayId;

    /**
     * 快速结办  0.非快速结办 1.快速结办
     */
    private Integer quickFinish;

    /**
     * 部门备注
     */
    private String remarkDept;

    /**
     * 科室备注
     */
    private String remarkOffice;

    /**
     * 核验
     */
    private Integer alarmStatus;
}
