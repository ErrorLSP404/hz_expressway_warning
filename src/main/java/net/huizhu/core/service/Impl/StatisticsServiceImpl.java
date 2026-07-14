package net.huizhu.core.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.huizhu.controller.vo.AlarmTypeDistributionVo;
import net.huizhu.controller.vo.EventTrendVo;
import net.huizhu.controller.vo.HighWayAlarmTypeSumVo;
import net.huizhu.controller.vo.SectionEventVo;
import net.huizhu.core.entity.AlarmLog;
import net.huizhu.core.entity.Highway;
import net.huizhu.core.entity.StatisticsAlarm;
import net.huizhu.core.service.IAlarmLogService;
import net.huizhu.core.service.IHighwayService;
import net.huizhu.core.service.IStatisticsAlarmService;
import net.huizhu.core.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@CacheConfig(cacheNames = "statistics")
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private IStatisticsAlarmService statisticsAlarmService;
    @Autowired
    private IAlarmLogService alarmLogService;
    @Autowired
    private IHighwayService highwayService;

    @Override
    @Cacheable(key = "#root.methodName.concat(':').concat(#startTime).concat('#').concat(#endTime)")
    public AlarmTypeDistributionVo typeDistribution(String startTime, String endTime) {
        Date startDate = DateUtil.beginOfDay(DateUtil.parse(startTime));
        Date endDate = DateUtil.endOfDay(DateUtil.parse(endTime));
        LocalDateTime start = LocalDateTimeUtil.of(startDate);
        LocalDateTime end = LocalDateTimeUtil.of(endDate);
        QueryWrapper<StatisticsAlarm> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ge(StatisticsAlarm::getStatisticsTime,start).le(StatisticsAlarm::getStatisticsTime,end);
        List<StatisticsAlarm> list = statisticsAlarmService.list(queryWrapper);
        AlarmTypeDistributionVo alarmTypeDistributionVo = new AlarmTypeDistributionVo();
        if(CollUtil.isNotEmpty(list)){
            int trafficEventSum = list.parallelStream().mapToInt(StatisticsAlarm::getTrafficEvent).sum();
            int pavementForeignMatterSum = list.parallelStream().mapToInt(StatisticsAlarm::getPavementForeignMatter).sum();
            int signDamageSum = list.parallelStream().mapToInt(StatisticsAlarm::getSignDamage).sum();
            int securityDamageSum = list.parallelStream().mapToInt(StatisticsAlarm::getSecurityDamage).sum();
            int illegalOccupationSum = list.parallelStream().mapToInt(StatisticsAlarm::getIllegalOccupation).sum();
            alarmTypeDistributionVo.setTrafficevent(trafficEventSum);
            alarmTypeDistributionVo.setPavementForeignMatter(pavementForeignMatterSum);
            alarmTypeDistributionVo.setSignDamage(signDamageSum);
            alarmTypeDistributionVo.setSecurityDamage(securityDamageSum);
            alarmTypeDistributionVo.setIllegalOccupation(illegalOccupationSum);
        }
        return alarmTypeDistributionVo;
    }

    @Override
    @Cacheable(key = "#root.methodName.concat(':').concat(#startTime).concat('#').concat(#endTime)")
    public List<HighWayAlarmTypeSumVo> highwayalarmtypesum(String startTime, String endTime) {
        Date startDate = DateUtil.beginOfDay(DateUtil.parse(startTime));
        Date endDate = DateUtil.endOfDay(DateUtil.parse(endTime));
        LocalDateTime start = LocalDateTimeUtil.of(startDate);
        LocalDateTime end = LocalDateTimeUtil.of(endDate);
        QueryWrapper<StatisticsAlarm> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ge(StatisticsAlarm::getStatisticsTime,start).le(StatisticsAlarm::getStatisticsTime,end);
        List<StatisticsAlarm> list = statisticsAlarmService.list(queryWrapper);
        List<HighWayAlarmTypeSumVo> highWayAlarmTypeSumVoList = new ArrayList<>();
        if(CollUtil.isNotEmpty(list)){
            Map<Long, List<StatisticsAlarm>> collect = list.parallelStream().collect(Collectors.groupingBy(StatisticsAlarm::getHighwayId));
            if(CollUtil.isNotEmpty(collect)){
                for (Map.Entry<Long, List<StatisticsAlarm>> highway_entry : collect.entrySet()) {
                    Long highwayId = highway_entry.getKey();
                    String highwayName = highway_entry.getValue().get(0).getHighwayName();
                    HighWayAlarmTypeSumVo highWayAlarmTypeSumVo = new HighWayAlarmTypeSumVo();
                    highWayAlarmTypeSumVo.setHighWayId(highwayId);
                    highWayAlarmTypeSumVo.setHighWayName(highwayName);
                    List<StatisticsAlarm> value = highway_entry.getValue();
                    int trafficEventSum = value.parallelStream().mapToInt(StatisticsAlarm::getTrafficEvent).sum();
                    int pavementForeignMatterSum = value.parallelStream().mapToInt(StatisticsAlarm::getPavementForeignMatter).sum();
                    int signDamageSum = value.parallelStream().mapToInt(StatisticsAlarm::getSignDamage).sum();
                    int securityDamageSum = value.parallelStream().mapToInt(StatisticsAlarm::getSecurityDamage).sum();
                    int illegalOccupationSum = value.parallelStream().mapToInt(StatisticsAlarm::getIllegalOccupation).sum();
                    highWayAlarmTypeSumVo.setTrafficevent(trafficEventSum);
                    highWayAlarmTypeSumVo.setPavementForeignMatter(pavementForeignMatterSum);
                    highWayAlarmTypeSumVo.setSignDamage(signDamageSum);
                    highWayAlarmTypeSumVo.setSecurityDamage(securityDamageSum);
                    highWayAlarmTypeSumVo.setIllegalOccupation(illegalOccupationSum);
                    highWayAlarmTypeSumVoList.add(highWayAlarmTypeSumVo);
                }
            }
        }
        return highWayAlarmTypeSumVoList;
    }

    @Override
    @Cacheable(key = "#root.methodName.concat(':').concat(#startTime).concat('#').concat(#endTime)")
    public List<SectionEventVo> sectionEventSum(String startTime, String endTime) {
        Date startDate = DateUtil.beginOfDay(DateUtil.parse(startTime));
        Date endDate = DateUtil.endOfDay(DateUtil.parse(endTime));
        LocalDateTime start = LocalDateTimeUtil.of(startDate);
        LocalDateTime end = LocalDateTimeUtil.of(endDate);
        QueryWrapper<StatisticsAlarm> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ge(StatisticsAlarm::getStatisticsTime,start).le(StatisticsAlarm::getStatisticsTime,end);
        List<StatisticsAlarm> list = statisticsAlarmService.list(queryWrapper);
        List<SectionEventVo> sectionEventVoList = new ArrayList<>();
        if(CollUtil.isNotEmpty(list)){
            Map<Long, List<StatisticsAlarm>> collect = list.parallelStream().collect(Collectors.groupingBy(StatisticsAlarm::getSectionId));
            if(CollUtil.isNotEmpty(collect)){
                for (Map.Entry<Long, List<StatisticsAlarm>>  section_entry : collect.entrySet()) {
                    Long sectionId = section_entry.getKey();
                    String sectionName = section_entry.getValue().get(0).getSectionName();
                    Long highwayId = section_entry.getValue().get(0).getHighwayId();
                    String highwayName = section_entry.getValue().get(0).getHighwayName();
                    SectionEventVo sectionEventVo = new SectionEventVo();
                    sectionEventVo.setSectionId(sectionId);
                    sectionEventVo.setSectionName(sectionName);
                    sectionEventVo.setHighWayId(highwayId);
                    sectionEventVo.setHighWayName(highwayName);
                    List<StatisticsAlarm> value = section_entry.getValue();
                    int trafficEventSum = value.parallelStream().mapToInt(StatisticsAlarm::getTrafficEvent).sum();
                    int pavementForeignMatterSum = value.parallelStream().mapToInt(StatisticsAlarm::getPavementForeignMatter).sum();
                    int signDamageSum = value.parallelStream().mapToInt(StatisticsAlarm::getSignDamage).sum();
                    int securityDamageSum = value.parallelStream().mapToInt(StatisticsAlarm::getSecurityDamage).sum();
                    int illegalOccupationSum = value.parallelStream().mapToInt(StatisticsAlarm::getIllegalOccupation).sum();
                    sectionEventVo.setTrafficevent(trafficEventSum);
                    sectionEventVo.setPavementForeignMatter(pavementForeignMatterSum);
                    sectionEventVo.setSignDamage(signDamageSum);
                    sectionEventVo.setSecurityDamage(securityDamageSum);
                    sectionEventVo.setIllegalOccupation(illegalOccupationSum);
                    sectionEventVoList.add(sectionEventVo);
                }
            }
        }
        return sectionEventVoList;
    }

    @Override
    @Cacheable(key = "#root.methodName.concat(':').concat(#startTime).concat('#').concat(#endTime)")
    public List<EventTrendVo> eventTrend(String startTime, String endTime) {
        Date startDate = DateUtil.beginOfDay(DateUtil.parse(startTime));
        Date endDate = DateUtil.endOfDay(DateUtil.parse(endTime));
        LocalDateTime start = LocalDateTimeUtil.of(startDate);
        LocalDateTime end = LocalDateTimeUtil.of(endDate);
        QueryWrapper<StatisticsAlarm> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ge(StatisticsAlarm::getStatisticsTime,start).le(StatisticsAlarm::getStatisticsTime,end);
        List<StatisticsAlarm> list = statisticsAlarmService.list(queryWrapper);
        List<EventTrendVo> eventTrendVoList = new ArrayList<>();
        if(CollUtil.isNotEmpty(list)){
            Map<LocalDateTime, List<StatisticsAlarm>> collect = list.parallelStream().collect(Collectors.groupingBy(StatisticsAlarm::getStatisticsTime));
            if(CollUtil.isNotEmpty(collect)){
                for (Map.Entry<LocalDateTime, List<StatisticsAlarm>>  event_entry : collect.entrySet()) {
                    LocalDateTime time = event_entry.getKey();
                    EventTrendVo eventTrendVo = new EventTrendVo();
                    eventTrendVo.setTime(time);
                    List<StatisticsAlarm> value = event_entry.getValue();
                    int trafficEventSum = value.parallelStream().mapToInt(StatisticsAlarm::getTrafficEvent).sum();
                    int pavementForeignMatterSum = value.parallelStream().mapToInt(StatisticsAlarm::getPavementForeignMatter).sum();
                    int signDamageSum = value.parallelStream().mapToInt(StatisticsAlarm::getSignDamage).sum();
                    int securityDamageSum = value.parallelStream().mapToInt(StatisticsAlarm::getSecurityDamage).sum();
                    int illegalOccupationSum = value.parallelStream().mapToInt(StatisticsAlarm::getIllegalOccupation).sum();
                    eventTrendVo.setTrafficevent(trafficEventSum);
                    eventTrendVo.setPavementForeignMatter(pavementForeignMatterSum);
                    eventTrendVo.setSignDamage(signDamageSum);
                    eventTrendVo.setSecurityDamage(securityDamageSum);
                    eventTrendVo.setIllegalOccupation(illegalOccupationSum);
                    eventTrendVoList.add(eventTrendVo);
                }
            }
        }
        return eventTrendVoList;
    }

    @Override
    public void revise(String reviseDate) {
        Date date = DateUtil.parse(reviseDate);
        String key_time = DateUtil.formatDate(date);
        Date begin = DateUtil.beginOfDay(date);
        Date endOfDay = DateUtil.endOfDay(date);
        int year = DateUtil.year(date);
        int month = DateUtil.month(date) + 1;
        LocalDateTime localDateTime = LocalDateTimeUtil.of(date);
        LocalDateTime start = LocalDateTimeUtil.of(begin);
        LocalDateTime end = LocalDateTimeUtil.of(endOfDay);
        QueryWrapper<AlarmLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ne(AlarmLog::getType,0).ge(AlarmLog::getAlarmTime,start).le(AlarmLog::getAlarmTime,end);
        List<AlarmLog> list = alarmLogService.list(queryWrapper);
        if(CollUtil.isNotEmpty(list)){
            //公路分组
            Map<Long, List<AlarmLog>> highway_collect = list.parallelStream().collect(Collectors.groupingBy(AlarmLog::getHighwayId));
            for (Map.Entry<Long, List<AlarmLog>> highway_entry : highway_collect.entrySet()) {
                //路段分组
                Long highway_id = highway_entry.getKey();
                Highway highway = highwayService.getById(highway_id);
                if(ObjectUtil.isNotNull(highway)){
                    String highwayName = highway.getHighwayName();
                    List<AlarmLog> highway_value = highway_entry.getValue();
                    Map<Long, List<AlarmLog>> section_collect = highway_value.parallelStream().collect(Collectors.groupingBy(AlarmLog::getSectionId));
                    for (Map.Entry<Long, List<AlarmLog>> section_entry : section_collect.entrySet()) {
                        Long section_id = section_entry.getKey();
                        List<AlarmLog> section_value = section_entry.getValue();
                        if(CollUtil.isNotEmpty(section_value)){
                            String sectionName = section_value.get(0).getSectionName();
                            int trafficevent = 0;
                            int pavementForeignMatter = 0;
                            int signDamage = 0;
                            int securityDamage = 0;
                            int illegalOccupation = 0;
                            for (AlarmLog alarmLog:section_value) {
                                Integer type = alarmLog.getType();
                                switch (type){
                                    case 1:
                                        trafficevent += 1;
                                        break;
                                    case 2:
                                        pavementForeignMatter += 1;
                                        break;
                                    case 3:
                                        signDamage += 1;
                                        break;
                                    case 4:
                                        securityDamage += 1;
                                        break;
                                    case 5:
                                        illegalOccupation += 1;
                                        break;
                                    default:
                                        break;
                                }
                            }
                            QueryWrapper<StatisticsAlarm> statisticsAlarmQueryWrapper = new QueryWrapper<>();
                            statisticsAlarmQueryWrapper.lambda().eq(StatisticsAlarm::getHighwayId,highway_id).
                                    eq(StatisticsAlarm::getSectionId,section_id).eq(StatisticsAlarm::getKeyTime,key_time);
                            StatisticsAlarm statisticsAlarm = statisticsAlarmService.getOne(statisticsAlarmQueryWrapper);
                            if(ObjectUtil.isNotNull(statisticsAlarm) ){
                                statisticsAlarm.setTrafficEvent(trafficevent);
                                statisticsAlarm.setPavementForeignMatter(pavementForeignMatter);
                                statisticsAlarm.setSignDamage(signDamage);
                                statisticsAlarm.setSecurityDamage(securityDamage);
                                statisticsAlarm.setIllegalOccupation(illegalOccupation);
                                statisticsAlarmService.updateById(statisticsAlarm);
                            }else {
                                statisticsAlarm = new StatisticsAlarm();
                                statisticsAlarm.setHighwayId(highway_id);
                                statisticsAlarm.setHighwayName(highwayName);
                                statisticsAlarm.setSectionId(section_id);
                                statisticsAlarm.setSectionName(sectionName);
                                statisticsAlarm.setYear(year);
                                statisticsAlarm.setMonth(month);
                                statisticsAlarm.setStatisticsTime(localDateTime);
                                statisticsAlarm.setTrafficEvent(trafficevent);
                                statisticsAlarm.setPavementForeignMatter(pavementForeignMatter);
                                statisticsAlarm.setSignDamage(signDamage);
                                statisticsAlarm.setSecurityDamage(securityDamage);
                                statisticsAlarm.setIllegalOccupation(illegalOccupation);
                                statisticsAlarm.setKeyTime(key_time);
                                statisticsAlarmService.save(statisticsAlarm);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    @CacheEvict(cacheNames = "statistics",allEntries=true)
    public boolean clearcache() {
        return true;
    }
}
