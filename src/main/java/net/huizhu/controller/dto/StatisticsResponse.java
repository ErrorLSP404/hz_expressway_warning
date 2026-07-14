package net.huizhu.controller.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.huizhu.controller.vo.*;

import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class StatisticsResponse extends BaseResponse{


    private StatisticsEventVo statisticsEventVo;

    private StatisticsTodayRealTimeVo statisticsTodayRealTimeVo;

    private AlarmTypeDistributionVo alarmTypeDistributionVo;

    private List<HighWayAlarmTypeSumVo> highWayAlarmTypeSumVoList;

    private List<SectionEventVo> sectionEventVoList;

    private List<EventTrendVo> eventTrendVoList;

}
