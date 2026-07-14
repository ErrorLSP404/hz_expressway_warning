package net.huizhu.core.service;


import net.huizhu.controller.vo.AlarmTypeDistributionVo;
import net.huizhu.controller.vo.EventTrendVo;
import net.huizhu.controller.vo.HighWayAlarmTypeSumVo;
import net.huizhu.controller.vo.SectionEventVo;

import java.util.List;

public interface StatisticsService {


    public AlarmTypeDistributionVo typeDistribution(String startTime, String endTime);

    public List<HighWayAlarmTypeSumVo> highwayalarmtypesum(String startTime, String endTime);

    public List<SectionEventVo> sectionEventSum(String startTime, String endTime);

    public List<EventTrendVo> eventTrend(String startTime, String endTime);

    public void revise(String reviseDate);

    public boolean clearcache();
}
