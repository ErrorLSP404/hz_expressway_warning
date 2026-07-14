package net.huizhu.controller.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class WorkOrderRequest {

    // 页码
    private Integer page = 1;
    // 个数
    private Integer pageSize = 10;
    // 0.admin派单 1.部门派单
    private Integer options;
    // 部门Id
    private Long deptId;
    // 时限
    private String timeOut;
    // 备注
    private String remark;
    // 事件类型
    private Integer type;
    // 报警id
    private Long alarmId;
    // 工单id
    private Long wordOrderId;
    // 报警id
    private List<Long> alarmIds;
    // 工单id
    private List<Long> wordOrderIds;
    // 用户标识
    private Integer roleTag;
    // 路线id
    private Long highwayId;
    // 历程桩号 id
    private Long sectionId;
    // 处理状态
    private Integer status;
    // 处理科室
    private Long officeId;
    // 起始时间
    private String startDateTime;
    // 结束时间
    private String endDateTime;

    // 工单Id集合
    private List<Long> wordOrderIdList;

    // 处理结果
    private String remarkOffice;

}
