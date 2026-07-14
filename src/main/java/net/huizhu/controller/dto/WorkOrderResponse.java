package net.huizhu.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.huizhu.controller.vo.StatisticsVo;
import net.huizhu.controller.vo.WorkOrderVo;

import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkOrderResponse {

    private List<WorkOrderVo> workOrderVoList;

    private Long total;

    private List<Long> revocationNo;

    private List<Long> timeOutNo;

    private StatisticsVo statisticsVo;

    private List<StatisticsVo> statisticsVoList;

}
