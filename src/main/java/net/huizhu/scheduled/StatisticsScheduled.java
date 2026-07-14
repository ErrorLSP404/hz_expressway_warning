package net.huizhu.scheduled;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.huizhu.core.entity.AlarmLog;
import net.huizhu.core.entity.Highway;
import net.huizhu.core.entity.StatisticsAlarm;
import net.huizhu.core.service.IAlarmLogService;
import net.huizhu.core.service.IHighwayService;
import net.huizhu.core.service.IStatisticsAlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StatisticsScheduled {

    @Autowired
    private IAlarmLogService alarmLogService;
    @Autowired
    private IStatisticsAlarmService statisticsAlarmService;
    @Autowired
    private IHighwayService highwayService;

    /**
     * 定时统计
     * 每天凌晨1点统计昨天数据
     */
    @Scheduled(cron = "0 0 1 * * ?")
    //@Scheduled(cron = "0 */1 * * * ?")
    public void statisticsJob() {
        Date yesterday = DateUtil.yesterday();
        String key_time = DateUtil.formatDate(yesterday);
        Date begin = DateUtil.beginOfDay(yesterday);
        Date endOfDay = DateUtil.endOfDay(yesterday);
        int year = DateUtil.year(yesterday);
        int month = DateUtil.month(yesterday) + 1;
        LocalDateTime localDateTime = LocalDateTimeUtil.of(yesterday);
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
}
