package net.huizhu.controller.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.huizhu.core.entity.WorkImage;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WorkOrderVo {

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
     * 部门名称
     */
    private String dName;
    /**
     * 科室名称
     */
    private String oName;
    /**
     * 办理时限
     */
    private String timeInfo;

    /**
     * 工单图片Vo
     */
    private List<WorkImage> workImage;

    /**
     * 是否超时 0.未超时 1.已超时
     */
    private Integer timeOut;

    /**
     * 时间格式化
     */
    private String startDataTime;

    /**
     * 超级管理员处理意见
     */
    private String remark;

    /**
     * 部门处理意见
     */
    private String remarkDept;

    /**
     * 事件描述
     */
    private String content;
    /**
     * 科室备注
     */
    private String remarkOffice;

    /**
     * 节点
     */
    private Integer node;

    /**
     * 超级管理员派单时间
     */
    private String adminSendOrderTime;

    /**
     * 部门派单时间
     */
    private String deptSendOrderTime;

    /**
     * 科室派单时间
     */
    private String officeSendOrderTime;

    /**
     * 摄像头信息
     */
    private CameraAndMessageVo cameraAndMessageVo;
}
