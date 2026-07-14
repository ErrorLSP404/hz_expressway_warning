package net.huizhu.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.huizhu.common.api.CommonResult;
import net.huizhu.common.constant.RedisConstant;
import net.huizhu.common.enums.*;
import net.huizhu.controller.dto.StatisticsRequest;
import net.huizhu.controller.dto.StatisticsResponse;
import net.huizhu.controller.dto.WorkOrderRequest;
import net.huizhu.controller.dto.WorkOrderResponse;
import net.huizhu.controller.vo.*;
import net.huizhu.core.entity.AlarmLog;
import net.huizhu.core.entity.AlarmScreenshot;
import net.huizhu.core.entity.CameraInfo;
import net.huizhu.core.entity.WorkOrder;
import net.huizhu.core.service.*;
import net.huizhu.rabbit.entity.AlarmParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private ICameraInfoService cameraInfoService;
    @Autowired
    private IAlarmLogService alarmLogService;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private IWorkOrderService workOrderService;
    @Autowired
    private IAlarmScreenshotService alarmScreenshotService;

    /**
     * 道路事件量统计
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/eventquantity")
    public CommonResult<StatisticsResponse> eventquantity(){
        QueryWrapper<CameraInfo> cameraInfoQueryWrapper = new QueryWrapper<>();
        cameraInfoQueryWrapper.lambda().eq(CameraInfo::getCameraState,1);
        int cameraNum = cameraInfoService.count(cameraInfoQueryWrapper);
        //当日时间
        Date now = new Date();
        DateTime beginOfDay = DateUtil.beginOfDay(now);
        DateTime endOfDay = DateUtil.endOfDay(now);
        LocalDateTime begin = LocalDateTimeUtil.of(beginOfDay);
        LocalDateTime end = LocalDateTimeUtil.of(endOfDay);
        QueryWrapper<AlarmLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ne(AlarmLog::getType,0).ge(AlarmLog::getAlarmTime,begin).le(AlarmLog::getAlarmTime,end);
        int dayNum = alarmLogService.count(queryWrapper);
        queryWrapper.clear();
        //当月
        DateTime beginOfMonth = DateUtil.beginOfMonth(now);
        DateTime endOfMonth = DateUtil.endOfMonth(now);
        LocalDateTime beginMonth = LocalDateTimeUtil.of(beginOfMonth);
        LocalDateTime endMonth = LocalDateTimeUtil.of(endOfMonth);
        queryWrapper.lambda().ne(AlarmLog::getType,0).ge(AlarmLog::getAlarmTime,beginMonth).le(AlarmLog::getAlarmTime,endMonth);
        int monthNum = alarmLogService.count(queryWrapper);
        //本年
        queryWrapper.clear();
        DateTime beginOfYear = DateUtil.beginOfYear(now);
        DateTime endOfYear = DateUtil.endOfYear(now);
        LocalDateTime beginyear = LocalDateTimeUtil.of(beginOfYear);
        LocalDateTime endYear = LocalDateTimeUtil.of(endOfYear);
        queryWrapper.lambda().ne(AlarmLog::getType,0).ge(AlarmLog::getAlarmTime,beginyear).le(AlarmLog::getAlarmTime,endYear);
        int yearNum = alarmLogService.count(queryWrapper);
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        StatisticsEventVo statisticsEventVo = new StatisticsEventVo();
        statisticsEventVo.setCameraNum(cameraNum);
        statisticsEventVo.setTodayEventNum(dayNum);
        statisticsEventVo.setMonthEventNum(monthNum);
        statisticsEventVo.setYearEventNum(yearNum);
        statisticsResponse.setStatisticsEventVo(statisticsEventVo);
        return CommonResult.success(statisticsResponse);
    }

    /**
     * 当日实时数据统计
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/todayrealtime")
    public CommonResult<StatisticsResponse> todayrealtime(){
        Date now = new Date();
        DateTime beginOfDay = DateUtil.beginOfDay(now);
        DateTime endOfDay = DateUtil.endOfDay(now);
        LocalDateTime begin = LocalDateTimeUtil.of(beginOfDay);
        LocalDateTime end = LocalDateTimeUtil.of(endOfDay);
        QueryWrapper<AlarmLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ne(AlarmLog::getType,0)
                .ge(AlarmLog::getAlarmTime,begin).le(AlarmLog::getAlarmTime,end)
                .orderByDesc(AlarmLog::getAlarmTime);
        List<AlarmLog> list = alarmLogService.list(queryWrapper);
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        StatisticsTodayRealTimeVo statisticsTodayRealTimeVo = new StatisticsTodayRealTimeVo();
        List<AlarmLogVo> alarmLogVoList = new ArrayList<>();
        if(CollUtil.isNotEmpty(list)){
            statisticsTodayRealTimeVo.setTotality(list.size());
            Integer trafficEventNum = 0;
            Integer pavementForeignMatterNum = 0;
            Integer signDamageNum = 0;
            Integer securityDamageNum = 0;
            Integer illegalOccupationNum = 0;
            Integer verified = 0;
            Integer notVerified = 0;
            for (AlarmLog alarmLog:list) {
                AlarmLogVo alarmLogVo = new AlarmLogVo();
                BeanUtil.copyProperties(alarmLog,alarmLogVo);
                String alarmParam = alarmLog.getAlarmParam();
                AlarmParameter alarmParameter = JSONUtil.toBean(alarmParam, AlarmParameter.class);
                alarmLogVo.setAlarmParameter(alarmParameter);
                String json = (String) redisTemplate.opsForHash().get(RedisConstant.CAMERA, String.valueOf(alarmLog.getCameraId()));
                if(StrUtil.isNotBlank(json)){
                    CameraInfo cameraInfo = JSONUtil.toBean(json, CameraInfo.class);
                    String cameraMapCoordinate = cameraInfo.getCameraMapCoordinate();
                    String longitude = StrUtil.subBefore(cameraMapCoordinate, "#", false);
                    String latitude = StrUtil.subAfter(cameraMapCoordinate,"#",false);
                    alarmLogVo.setLongitude(longitude);
                    alarmLogVo.setLatitude(latitude);
                }
                alarmLogVoList.add(alarmLogVo);
                Integer status = alarmLog.getStatus();
                switch (status){
                    case 0:
                        notVerified += 1;
                        break;
                    case 1:
                        verified += 1;
                        break;
                    default:
                        break;
                }
                Integer type = alarmLog.getType();
                switch (type){
                    case 1:
                        trafficEventNum += 1;
                        break;
                    case 2:
                        pavementForeignMatterNum += 1;
                        break;
                    case 3:
                        signDamageNum += 1;
                        break;
                    case 4:
                        securityDamageNum += 1;
                        break;
                    case 5:
                        illegalOccupationNum += 1;
                        break;
                    default:
                        break;
                }
            }
            statisticsTodayRealTimeVo.setNotVerified(notVerified);
            statisticsTodayRealTimeVo.setVerified(verified);
            statisticsTodayRealTimeVo.setTrafficEventNum(trafficEventNum);
            statisticsTodayRealTimeVo.setPavementForeignMatterNum(pavementForeignMatterNum);
            statisticsTodayRealTimeVo.setSignDamageNum(signDamageNum);
            statisticsTodayRealTimeVo.setSecurityDamageNum(securityDamageNum);
            statisticsTodayRealTimeVo.setIllegalOccupationNum(illegalOccupationNum);
        }
        statisticsTodayRealTimeVo.setAlarmLogVoList(alarmLogVoList);
        statisticsResponse.setStatisticsTodayRealTimeVo(statisticsTodayRealTimeVo);
        return CommonResult.success(statisticsResponse);
    }

    /**
     * 当日实时数据统计（按照算法分类）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/todayalgorithm")
    public CommonResult<StatisticsResponse> todayalgorithm(@RequestBody StatisticsRequest statisticsRequest){
        Integer algorithmNum = statisticsRequest.getAlgorithmNum();
        Date now = new Date();
        DateTime beginOfDay = DateUtil.beginOfDay(now);
        DateTime endOfDay = DateUtil.endOfDay(now);
        LocalDateTime begin = LocalDateTimeUtil.of(beginOfDay);
        LocalDateTime end = LocalDateTimeUtil.of(endOfDay);
        QueryWrapper<AlarmLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AlarmLog::getAlgorithmNum,algorithmNum).ne(AlarmLog::getType,0)
                .ge(AlarmLog::getAlarmTime,begin).le(AlarmLog::getAlarmTime,end)
                .orderByDesc(AlarmLog::getAlarmTime);
        List<AlarmLog> list = alarmLogService.list(queryWrapper);
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        StatisticsTodayRealTimeVo statisticsTodayRealTimeVo = new StatisticsTodayRealTimeVo();
        List<AlarmLogVo> alarmLogVoList = new ArrayList<>();
        if(CollUtil.isNotEmpty(list)){
            statisticsTodayRealTimeVo.setTotality(list.size());
            Integer verified = 0;
            Integer notVerified = 0;
            for (AlarmLog alarmLog:list) {
                AlarmLogVo alarmLogVo = new AlarmLogVo();
                BeanUtil.copyProperties(alarmLog,alarmLogVo);
                Long id = alarmLogVo.getId();
                QueryWrapper<AlarmScreenshot> alarmScreenshotQueryWrapper = new QueryWrapper<>();
                alarmScreenshotQueryWrapper.lambda().eq(AlarmScreenshot::getAlarmId,id);
                List<AlarmScreenshot> alarmScreenshotList = alarmScreenshotService.list(alarmScreenshotQueryWrapper);
                List<AlarmScreenshotVo> alarmScreenshotVoList = BeanUtil.copyToList(alarmScreenshotList, AlarmScreenshotVo.class, CopyOptions.create());
                alarmLogVo.setScreenshotList(alarmScreenshotVoList);
                String alarmParam = alarmLog.getAlarmParam();
                AlarmParameter alarmParameter = JSONUtil.toBean(alarmParam, AlarmParameter.class);
                alarmLogVo.setAlarmParameter(alarmParameter);
                String json = (String) redisTemplate.opsForHash().get(RedisConstant.CAMERA, String.valueOf(alarmLog.getCameraId()));
                if(StrUtil.isNotBlank(json)){
                    CameraInfo cameraInfo = JSONUtil.toBean(json, CameraInfo.class);
                    String cameraMapCoordinate = cameraInfo.getCameraMapCoordinate();
                    String longitude = StrUtil.subBefore(cameraMapCoordinate, "#", false);
                    String latitude = StrUtil.subAfter(cameraMapCoordinate,"#",false);
                    alarmLogVo.setLongitude(longitude);
                    alarmLogVo.setLatitude(latitude);
                }
                alarmLogVoList.add(alarmLogVo);
                Integer status = alarmLog.getStatus();
                switch (status){
                    case 0:
                        notVerified += 1;
                        break;
                    case 1:
                        verified += 1;
                        break;
                    default:
                        break;
                }
            }
            statisticsTodayRealTimeVo.setNotVerified(notVerified);
            statisticsTodayRealTimeVo.setVerified(verified);
        }
        statisticsTodayRealTimeVo.setAlarmLogVoList(alarmLogVoList);
        statisticsResponse.setStatisticsTodayRealTimeVo(statisticsTodayRealTimeVo);
        return CommonResult.success(statisticsResponse);
    }


    /**
     * 交通事件类型分布
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/typeDistribution")
    public CommonResult<StatisticsResponse> typeDistribution(@RequestBody StatisticsRequest statisticsRequest){
        String startTime = statisticsRequest.getStartTime();
        String endTime = statisticsRequest.getEndTime();
        if(StrUtil.isBlank(startTime)){
            return CommonResult.failed("请填写统计时间段");
        }
        if(StrUtil.isBlank(endTime)){
            return CommonResult.failed("请填写统计时间段");
        }
        AlarmTypeDistributionVo alarmTypeDistributionVo = statisticsService.typeDistribution(startTime,endTime);
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        statisticsResponse.setAlarmTypeDistributionVo(alarmTypeDistributionVo);
        return CommonResult.success(statisticsResponse);
    }


    /**
     * 路段事件高发量排名
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/highwayalarmtypesum")
    public CommonResult<StatisticsResponse> highwayalarmtypesum(@RequestBody StatisticsRequest statisticsRequest){

        String startTime = statisticsRequest.getStartTime();
        String endTime = statisticsRequest.getEndTime();
        if(StrUtil.isBlank(startTime)){
            return CommonResult.failed("请填写统计时间段");
        }
        if(StrUtil.isBlank(endTime)){
            return CommonResult.failed("请填写统计时间段");
        }
        List<HighWayAlarmTypeSumVo> highWayAlarmTypeSumVoList = statisticsService.highwayalarmtypesum(startTime, endTime);
        //排序(倒序)
        highWayAlarmTypeSumVoList = highWayAlarmTypeSumVoList.parallelStream().sorted(Comparator.comparing(HighWayAlarmTypeSumVo::getSum).reversed()).collect(Collectors.toList());
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        statisticsResponse.setHighWayAlarmTypeSumVoList(highWayAlarmTypeSumVoList);
        return CommonResult.success(statisticsResponse);
    }


    /**
     * 线路&里程桩号事件高发量排名
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/sectioneventsumtop5")
    public CommonResult<StatisticsResponse> sectionEventSumTop5(@RequestBody StatisticsRequest statisticsRequest){
        String startTime = statisticsRequest.getStartTime();
        String endTime = statisticsRequest.getEndTime();
        if(StrUtil.isBlank(startTime)){
            return CommonResult.failed("请填写统计时间段");
        }
        if(StrUtil.isBlank(endTime)){
            return CommonResult.failed("请填写统计时间段");
        }
        List<SectionEventVo> sectionEventVoList = statisticsService.sectionEventSum(startTime, endTime);
        sectionEventVoList = sectionEventVoList.parallelStream().sorted(Comparator.comparing(SectionEventVo::getSum).reversed()).collect(Collectors.toList());
        if(sectionEventVoList.size() > 5){
            sectionEventVoList = CollUtil.sub(sectionEventVoList, 0, 5);
        }
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        statisticsResponse.setSectionEventVoList(sectionEventVoList);
        return CommonResult.success(statisticsResponse);

    }


    /**
     * 各类道路事件发生趋势
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/eventtrend")
    public CommonResult<StatisticsResponse> eventTrend(@RequestBody StatisticsRequest statisticsRequest){
        String startTime = statisticsRequest.getStartTime();
        String endTime = statisticsRequest.getEndTime();
        if(StrUtil.isBlank(startTime)){
            return CommonResult.failed("请填写统计时间段");
        }
        if(StrUtil.isBlank(endTime)){
            return CommonResult.failed("请填写统计时间段");
        }
        List<EventTrendVo> eventTrendVoList = statisticsService.eventTrend(startTime, endTime);
        eventTrendVoList = eventTrendVoList.parallelStream().sorted(Comparator.comparing(EventTrendVo::getTime)).collect(Collectors.toList());
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        statisticsResponse.setEventTrendVoList(eventTrendVoList);
        return CommonResult.success(statisticsResponse);
    }

    /**
     * 已结办平均时间统计
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/orderEndAvg")
    public CommonResult<WorkOrderResponse> orderEndAvg(@RequestBody WorkOrderRequest workOrderRequest){

        // 定义默认循环次数
        long monthSize = 6;

        LocalDateTime endDateTime = null;
        LocalDateTime startDateTimeFor = null;
        LocalDateTime startDateTime = null;

        if (StrUtil.isNotBlank(workOrderRequest.getStartDateTime())
                && StrUtil.isNotBlank(workOrderRequest.getEndDateTime())) {
            // 获取所选月的最后一天
            endDateTime = LocalDateTimeUtil.parseDate(workOrderRequest.getEndDateTime(),DateTimeFormatter.ofPattern("yyyy-MM"))
                    .with(TemporalAdjusters.lastDayOfMonth())
                    .atTime(23, 59, 59);
            startDateTimeFor = LocalDateTimeUtil.beginOfDay(endDateTime.with(TemporalAdjusters.firstDayOfMonth()));
            // 获取所选月的第一天
            startDateTime = LocalDateTimeUtil.parseDate(workOrderRequest.getStartDateTime(),DateTimeFormatter.ofPattern("yyyy-MM"))
                    .with(TemporalAdjusters.firstDayOfMonth())
                    .atTime(0,0,0);

            // 判断统计时间是否填写正确
            if (startDateTime.compareTo(endDateTime) > 0) {
                return CommonResult.failed("统计的开始月份不能大于结束月份");
            }

            // 计算循环个数
            monthSize = LocalDateTimeUtil.between(startDateTime, endDateTime, ChronoUnit.MONTHS);
            monthSize = monthSize > 0 ? monthSize + 1 : 1; // 填写一个月为0 默认为1

        } else {
            LocalDateTime now = LocalDateTime.now();
            // 本月最后一天
            endDateTime =
                    LocalDateTimeUtil.endOfDay(now.with(TemporalAdjusters.lastDayOfMonth()));
            // 本月第一天
            startDateTimeFor =
                    LocalDateTimeUtil.beginOfDay(now.with(TemporalAdjusters.firstDayOfMonth()));
            // 6个月前第一天
            startDateTime =
                    LocalDateTimeUtil.beginOfDay(now.minusMonths(5).with(TemporalAdjusters.firstDayOfMonth()));
        }

        QueryWrapper<WorkOrder> workOrderQueryWrapper = new QueryWrapper<>();
        workOrderQueryWrapper.lambda().between(WorkOrder::getGmtCreate,startDateTime,endDateTime)
                .eq(WorkOrder::getStatus, WorkOrderEnum.FINISH.getCode());

        List<WorkOrder> workOrderList = workOrderService.list(workOrderQueryWrapper);

        LocalDateTime localEndTime = endDateTime;
        LocalDateTime localStartTime = startDateTimeFor;

        List<StatisticsVo> statisticsVos = new ArrayList<>();
        for (int i = 0; i < monthSize; i++) {
            long timeOfHour = 0L;
            long listSize = 0L;

            if (i > 0) {
                localEndTime = endDateTime.minusMonths(i).with(TemporalAdjusters.lastDayOfMonth());
                localStartTime = startDateTimeFor.minusMonths(i).with(TemporalAdjusters.firstDayOfMonth());

            }
            if (CollUtil.isNotEmpty(workOrderList)) {
                for (WorkOrder workOrder:workOrderList) {
                    LocalDateTime gmtCreate = workOrder.getGmtCreate();
                    // 判断创建时间是否属于两个月份之间的时间
                    if (gmtCreate.isBefore(LocalDateTimeUtil.endOfDay(localEndTime)) && gmtCreate.isAfter(localStartTime)) {
                        // 获取工单耗时
                        if (workOrder.getOrderTime() != null &&
                                workOrder.getQuickFinish().equals(WorkQuickEnum.SLOW.getCode())) {

                            long start = workOrder.getOrderTime().toEpochSecond(ZoneOffset.ofHours(0));
                            long ene = workOrder.getGmtModified().toEpochSecond(ZoneOffset.ofHours(0));
                            timeOfHour += (ene - start); // 获取用时时间戳
                            listSize++;
                        }
                    }
                }
            }
            StatisticsVo statisticsVo = new StatisticsVo();

            // 获取小时&分钟
            String timeOfh;
            timeOfh = avgTimeOfString(timeOfHour,listSize);

            statisticsVo.setElapsedTime(timeOfh);
            statisticsVo.setDateTime(localStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            statisticsVo.setLocalDateTime(localStartTime);
            statisticsVos.add(statisticsVo);
        }
        List<StatisticsVo> statisticsVoList =
                statisticsVos.stream().sorted(Comparator.comparing(StatisticsVo::getLocalDateTime)).collect(Collectors.toList());
        WorkOrderResponse workOrderResponse = new WorkOrderResponse();
        workOrderResponse.setStatisticsVoList(statisticsVoList);
        return CommonResult.success(workOrderResponse);
    }


    /**
     * 不同事件类型平均处理时间
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/orderTypeAvg")
    public CommonResult<WorkOrderResponse> orderTypeAvg(@RequestBody WorkOrderRequest workOrderRequest){
        // 定义默认循环次数
        long monthSize = 6;

        LocalDateTime endDateTime = null;
        LocalDateTime startDateTimeFor = null;
        LocalDateTime startDateTime = null;

        if (StrUtil.isNotBlank(workOrderRequest.getStartDateTime())
                && StrUtil.isNotBlank(workOrderRequest.getEndDateTime())) {
            // 获取所选月的最后一天
            endDateTime = LocalDateTimeUtil.parseDate(workOrderRequest.getEndDateTime(),DateTimeFormatter.ofPattern("yyyy-MM"))
                    .with(TemporalAdjusters.lastDayOfMonth())
                    .atTime(23, 59, 59);
            startDateTimeFor = LocalDateTimeUtil.beginOfDay(endDateTime.with(TemporalAdjusters.firstDayOfMonth()));
            // 获取所选月的第一天
            startDateTime = LocalDateTimeUtil.parseDate(workOrderRequest.getStartDateTime(),DateTimeFormatter.ofPattern("yyyy-MM"))
                    .with(TemporalAdjusters.firstDayOfMonth())
                    .atTime(0,0,0);

            // 判断统计时间是否填写正确
            if (startDateTime.compareTo(endDateTime) > 0) {
                return CommonResult.failed("统计的开始月份不能大于结束月份");
            }

            // 计算循环个数
            monthSize = LocalDateTimeUtil.between(startDateTime, endDateTime, ChronoUnit.MONTHS);
            monthSize = monthSize > 0 ? monthSize + 1 : 1; // 填写一个月为0 默认为1

        } else {
            LocalDateTime now = LocalDateTime.now();
            // 本月最后一天
            endDateTime =
                    LocalDateTimeUtil.endOfDay(now.with(TemporalAdjusters.lastDayOfMonth()));
            // 本月第一天
            startDateTimeFor =
                    LocalDateTimeUtil.beginOfDay(now.with(TemporalAdjusters.firstDayOfMonth()));
            // 6个月前第一天
            startDateTime =
                    LocalDateTimeUtil.beginOfDay(now.minusMonths(5).with(TemporalAdjusters.firstDayOfMonth()));
        }

        //  获取6个月内所有已结办数据
        QueryWrapper<WorkOrder> workOrderQueryWrapper = new QueryWrapper<>();
        workOrderQueryWrapper.lambda().between(WorkOrder::getGmtCreate,startDateTime,endDateTime)
                .eq(WorkOrder::getStatus, WorkOrderEnum.FINISH.getCode());

        List<WorkOrder> workOrderList = workOrderService.list(workOrderQueryWrapper);
        LocalDateTime localEndTime = endDateTime;
        LocalDateTime localStartTime = startDateTimeFor;
        List<StatisticsVo> statisticsVos = new ArrayList<>();
        for (int i = 0; i < monthSize; i++) {
            // 不同事件类型定义
            long nothing = 0L;
            long nothingSize = 0L;
            long trafficEvent = 0L;
            long trafficEventSize = 0L;
            long pavementForeignMatter = 0L;
            long pavementForeignMatterSize = 0L;
            long signDamage = 0L;
            long signDamageSize = 0L;
            long securityDamage = 0L;
            long securityDamageSize = 0L;
            long illegalOccupation = 0L;
            long illegalOccupationSize = 0L;

            if (i > 0) {
                localEndTime = endDateTime.minusMonths(i).with(TemporalAdjusters.lastDayOfMonth());
                localStartTime = startDateTimeFor.minusMonths(i).with(TemporalAdjusters.firstDayOfMonth());
            }
            if (CollUtil.isNotEmpty(workOrderList)) {
                for (WorkOrder workOrder:workOrderList) {
                    LocalDateTime gmtCreate = workOrder.getGmtCreate();
                    // 判断创建时间是否属于两个月份之间的时间
                    if (gmtCreate.isBefore(LocalDateTimeUtil.endOfDay(localEndTime))
                            && gmtCreate.isAfter(localStartTime)) {

                        if (workOrder.getOrderTime() != null &&
                                workOrder.getQuickFinish().equals(WorkQuickEnum.SLOW.getCode())) { // 快速结办，不参与统计
                            // 获取工单耗时
                            long start = workOrder.getOrderTime().toEpochSecond(ZoneOffset.ofHours(0));
                            long ene = workOrder.getGmtModified().toEpochSecond(ZoneOffset.ofHours(0));
                            switch (workOrder.getType()) {
                                case 0: // 无事发生
                                    nothing += (ene - start);
                                    nothingSize++;
                                    break;
                                case 1: // 交通事件
                                    trafficEvent += (ene - start);
                                    trafficEventSize++;
                                    break;
                                case 2: // 路面异物
                                    pavementForeignMatter += (ene - start);
                                    pavementForeignMatterSize++;
                                    break;
                                case 3: // 公路标志标线损坏
                                    signDamage += (ene - start);
                                    signDamageSize++;
                                    break;
                                case 4: // 安防设施损坏
                                    securityDamage += (ene - start);
                                    securityDamageSize++;
                                    break;
                                case 5: // 非法占用公路行为
                                    illegalOccupation += (ene - start);
                                    illegalOccupationSize++;
                                    break;
                            }
                        }
                    }
                }
            }

            StatisticsVo statisticsVo = new StatisticsVo();
            statisticsVo.setNothingTime(avgTimeOfString(nothing, nothingSize));
            statisticsVo.setTrafficEventTime(avgTimeOfString(trafficEvent,trafficEventSize));
            statisticsVo.setPavementForeignMatterTime(avgTimeOfString(pavementForeignMatter, pavementForeignMatterSize));
            statisticsVo.setSignDamageTime(avgTimeOfString(signDamage,signDamageSize));
            statisticsVo.setSecurityDamageTime(avgTimeOfString(securityDamage,securityDamageSize));
            statisticsVo.setIllegalOccupationTime(avgTimeOfString(illegalOccupation,illegalOccupationSize));
            statisticsVo.setDateTime(localStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            statisticsVo.setLocalDateTime(localStartTime);
            statisticsVos.add(statisticsVo);
        }
        List<StatisticsVo> statisticsVoList =
                statisticsVos.stream().sorted(Comparator.comparing(StatisticsVo::getLocalDateTime)).collect(Collectors.toList());
        WorkOrderResponse workOrderResponse = new WorkOrderResponse();
        workOrderResponse.setStatisticsVoList(statisticsVoList);
        return CommonResult.success(workOrderResponse);
    }

    /**
     * 获取近六个月的统计
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/recentSixMonth")
    public CommonResult<WorkOrderResponse> recentSixMonth(@RequestBody WorkOrderRequest workOrderRequest) {

        Integer options = workOrderRequest.getOptions();
        if (options == null) {
            return CommonResult.failed("选项不能为空");
        }

        // 定义默认循环次数
        long monthSize = 6;

        LocalDateTime endDateTime = null;
        LocalDateTime startDateTimeFor = null;
        LocalDateTime startDateTime = null;

        if (StrUtil.isNotBlank(workOrderRequest.getStartDateTime())
                && StrUtil.isNotBlank(workOrderRequest.getEndDateTime())) {
            // 获取所选月的最后一天
            endDateTime = LocalDateTimeUtil.parseDate(workOrderRequest.getEndDateTime(),DateTimeFormatter.ofPattern("yyyy-MM"))
                    .with(TemporalAdjusters.lastDayOfMonth())
                    .atTime(23, 59, 59);
            startDateTimeFor = LocalDateTimeUtil.beginOfDay(endDateTime.with(TemporalAdjusters.firstDayOfMonth()));
            // 获取所选月的第一天
            startDateTime = LocalDateTimeUtil.parseDate(workOrderRequest.getStartDateTime(),DateTimeFormatter.ofPattern("yyyy-MM"))
                    .with(TemporalAdjusters.firstDayOfMonth())
                    .atTime(0,0,0);

            // 判断统计时间是否填写正确
            if (startDateTime.compareTo(endDateTime) > 0) {
                return CommonResult.failed("统计的开始月份不能大于结束月份");
            }

            // 计算循环个数
            monthSize = LocalDateTimeUtil.between(startDateTime, endDateTime, ChronoUnit.MONTHS);
            monthSize = monthSize > 0 ? monthSize + 1 : 1; // 填写一个月为0 默认为1

        } else {
            LocalDateTime now = LocalDateTime.now();
            // 本月最后一天
            endDateTime =
                    LocalDateTimeUtil.endOfDay(now.with(TemporalAdjusters.lastDayOfMonth()));
            // 本月第一天
            startDateTimeFor =
                    LocalDateTimeUtil.beginOfDay(now.with(TemporalAdjusters.firstDayOfMonth()));
            // 6个月前第一天
            startDateTime =
                    LocalDateTimeUtil.beginOfDay(now.minusMonths(5).with(TemporalAdjusters.firstDayOfMonth()));
        }

        // 获取未处理数据
        QueryWrapper<WorkOrder> workOrderQueryWrapper = new QueryWrapper<>();
        workOrderQueryWrapper.lambda().between(WorkOrder::getGmtCreate,startDateTime,endDateTime);
        List<WorkOrder> workOrders = workOrderService.list(workOrderQueryWrapper);

        LocalDateTime localEndTime = endDateTime;
        LocalDateTime localStartTime = startDateTimeFor;

        List<StatisticsVo> statisticsVos = new ArrayList<>();
        for (int i = 0; i < monthSize; i++) {
            long untreatedCount = 0L; // 未派单
            long processedCount = 0L; // 已派单
            long finishCount = 0L; // 已结办
            long timeOut = 0L; //已超时

            long danYang = 0L;
            long danTu = 0L;
            long pardon = 0L;
            long yangZhong = 0L;
            long city = 0L;
            long threeTwoOne = 0L;


            StatisticsVo statisticsVo = new StatisticsVo();
            if (i > 0) {
                localEndTime = endDateTime.minusMonths(i).with(TemporalAdjusters.lastDayOfMonth());
                localStartTime = startDateTimeFor.minusMonths(i).with(TemporalAdjusters.firstDayOfMonth());
            }
            if (CollUtil.isNotEmpty(workOrders)) {
                for (WorkOrder workOrder : workOrders) {
                    LocalDateTime gmtCreate = workOrder.getGmtCreate();
                    // 判断创建时间是否属于两个月份之间的时间
                    if (gmtCreate.isBefore(LocalDateTimeUtil.endOfDay(localEndTime)) && gmtCreate.isAfter(localStartTime)) {
                        if (options.equals(0)) {
                            switch (workOrder.getStatus()) {
                                case 0: // 未派单
                                    untreatedCount++;
                                    break;
                                case 1: // 已派单
                                    processedCount++;
                                    break;
                                case 2: // 已结办
                                    finishCount++;
                                    break;
                            }
                            if (workOrder.getTimeOut() != null
                                    && workOrder.getTimeOut().equals(TimeOutEnum.TIMEOUT_YES.getCode())) {
                                timeOut++;
                            }
                        } else {
                            Long oId = workOrder.getOId();
                            if (oId != null && !oId.equals(-1L)) {
                                if (oId.equals(OfficeEnum.DAN_YANG.getCode())) {
                                    danYang++;
                                } else if (oId.equals(OfficeEnum.DAN_TU.getCode())) {
                                    danTu++;
                                } else if (oId.equals(OfficeEnum.BAO_RONG.getCode())) {
                                    pardon++;
                                } else if (oId.equals(OfficeEnum.YANG_ZHONG.getCode())) {
                                    yangZhong++;
                                } else if (oId.equals(OfficeEnum.CITY.getCode())) {
                                    city++;
                                } else if (oId.equals(OfficeEnum.THREE.getCode())) {
                                    threeTwoOne++;
                                }
                            }

                        }
                    }
                }
            }
            if (options.equals(0)) {
                // 赋值
                statisticsVo.setUntreatedCount(untreatedCount);
                statisticsVo.setProcessedCount(processedCount);
                statisticsVo.setFinishCount(finishCount);
                statisticsVo.setTimeOut(timeOut);
                statisticsVos.add(statisticsVo);
            }
            if (options.equals(1)){
                // 赋值
                statisticsVo.setDanYang(danYang);
                statisticsVo.setDanTu(danTu);
                statisticsVo.setPardon(pardon);
                statisticsVo.setYangZhong(yangZhong);
                statisticsVo.setCity(city);
                statisticsVo.setThreeTwoOne(threeTwoOne);
                statisticsVos.add(statisticsVo);
            }
            statisticsVo.setDateTime(localStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            statisticsVo.setLocalDateTime(localStartTime);
        }
        List<StatisticsVo> statisticsVoList =
                statisticsVos.stream().sorted(Comparator.comparing(StatisticsVo::getLocalDateTime)).collect(Collectors.toList());
        WorkOrderResponse workOrderResponse = new WorkOrderResponse();
        workOrderResponse.setStatisticsVoList(statisticsVoList);
        return CommonResult.success(workOrderResponse);
    }


    /**
     * 全部事件处理状态
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/orderStatus")
    public CommonResult<WorkOrderResponse> orderStatus(@RequestBody WorkOrderRequest workOrderRequest){

        LocalDateTime endDateTime = null;
        LocalDateTime startDateTime = null;
        if (StrUtil.isNotBlank(workOrderRequest.getStartDateTime())
                && StrUtil.isNotBlank(workOrderRequest.getEndDateTime())) {
            // 获取所选月的最后一天
            endDateTime = LocalDateTimeUtil.parseDate(workOrderRequest.getEndDateTime(),DateTimeFormatter.ofPattern("yyyy-MM"))
                    .with(TemporalAdjusters.lastDayOfMonth())
                    .atTime(23, 59, 59);
            // 获取所选月的第一天
            startDateTime = LocalDateTimeUtil.parseDate(workOrderRequest.getStartDateTime(),DateTimeFormatter.ofPattern("yyyy-MM"))
                    .with(TemporalAdjusters.firstDayOfMonth())
                    .atTime(0,0,0);

            // 判断统计时间是否填写正确
            if (startDateTime.compareTo(endDateTime) > 0) {
                return CommonResult.failed("统计的开始月份不能大于结束月份");
            }
        } else {
            LocalDateTime now = LocalDateTime.now();
            // 本月最后一天
            endDateTime =
                    LocalDateTimeUtil.endOfDay(now.with(TemporalAdjusters.lastDayOfMonth()));
            // 6个月前第一天
            startDateTime =
                    LocalDateTimeUtil.beginOfDay(now.minusMonths(6).with(TemporalAdjusters.firstDayOfMonth()));
        }
        Map<String,Object> map = new HashMap<>();
        map.put("startDateTime",startDateTime);
        map.put("endDateTime",endDateTime);
        map.put("status",WorkOrderEnum.STATUS.getCode());
        Long untreatedCount = workOrderService.statisticsOrder(map);
        map.put("status",WorkOrderEnum.FINISH.getCode());
        Long finishCount = workOrderService.statisticsOrder(map);

        map.put("status",WorkOrderEnum.PROCESSING.getCode());
        Long processedCount = workOrderService.statisticsOrder(map);
        map.put("timeOut",TimeOutEnum.TIMEOUT_YES.getCode());
        Long timeOutCount = workOrderService.statisticsOrder(map);
        WorkOrderResponse workOrderResponse = new WorkOrderResponse();
        StatisticsVo statisticsVo = new StatisticsVo();

        statisticsVo.setUntreatedCount(untreatedCount);
        statisticsVo.setProcessedCount(processedCount);
        statisticsVo.setFinishCount(finishCount);
        statisticsVo.setTimeOut(timeOutCount);
        workOrderResponse.setStatisticsVo(statisticsVo);
        return CommonResult.success(workOrderResponse);
    }

    /**
     * 统计不同类型事件发生数量
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/orderType")
    public CommonResult<WorkOrderResponse> orderType(@RequestBody WorkOrderRequest workOrderRequest){

        LocalDateTime endDateTime = null;
        LocalDateTime startDateTime = null;
        if (StrUtil.isNotBlank(workOrderRequest.getStartDateTime())
                && StrUtil.isNotBlank(workOrderRequest.getEndDateTime())) {
            // 获取所选月的最后一天
            endDateTime = LocalDateTimeUtil.parseDate(workOrderRequest.getEndDateTime(),DateTimeFormatter.ofPattern("yyyy-MM"))
                    .with(TemporalAdjusters.lastDayOfMonth())
                    .atTime(23, 59, 59);
            // 获取所选月的第一天
            startDateTime = LocalDateTimeUtil.parseDate(workOrderRequest.getStartDateTime(),DateTimeFormatter.ofPattern("yyyy-MM"))
                    .with(TemporalAdjusters.firstDayOfMonth())
                    .atTime(0,0,0);

            // 判断统计时间是否填写正确
            if (startDateTime.compareTo(endDateTime) > 0) {
                return CommonResult.failed("统计的开始月份不能大于结束月份");
            }
        } else {
            LocalDateTime now = LocalDateTime.now();
            // 本月最后一天
            endDateTime =
                    LocalDateTimeUtil.endOfDay(now.with(TemporalAdjusters.lastDayOfMonth()));
            // 6个月前第一天
            startDateTime =
                    LocalDateTimeUtil.beginOfDay(now.minusMonths(6).with(TemporalAdjusters.firstDayOfMonth()));
        }

        Map<String,Object> map = new HashMap<>();
        map.put("startDateTime",startDateTime);
        map.put("endDateTime",endDateTime);

        StatisticsVo statisticsVo = new StatisticsVo();

        for (AlarmTypeEnum alarmTypeEnum:AlarmTypeEnum.values()) {
            Integer code = alarmTypeEnum.getCode();
            map.put("type",code);
            Long aLong = workOrderService.statisticsOrder(map);
            if (code.equals(AlarmTypeEnum.NOTHING.getCode())) {
                statisticsVo.setNothing(aLong);
            } else if (code.equals(AlarmTypeEnum.TrafficEvent.getCode())) {
                statisticsVo.setTrafficEvent(aLong);
            } else if (code.equals(AlarmTypeEnum.PavementForeignMatter.getCode())){
                statisticsVo.setPavementForeignMatter(aLong);
            } else if (code.equals(AlarmTypeEnum.SignDamage.getCode())) {
                statisticsVo.setSignDamage(aLong);
            } else if (code.equals(AlarmTypeEnum.SecurityDamage.getCode())) {
                statisticsVo.setSecurityDamage(aLong);
            } else if (code.equals(AlarmTypeEnum.IllegalOccupation.getCode())) {
                statisticsVo.setIllegalOccupation(aLong);
            }
        }
        WorkOrderResponse workOrderResponse = new WorkOrderResponse();
        workOrderResponse.setStatisticsVo(statisticsVo);
        return CommonResult.success(workOrderResponse);
    }
    


    /**
     * 修正统计
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/revise")
    public CommonResult revise(@RequestBody StatisticsRequest statisticsRequest){
        String reviseDate = statisticsRequest.getReviseDate();
        if(StrUtil.isBlank(reviseDate)){
            return CommonResult.failed("请填写修正日期");
        }
        statisticsService.revise(reviseDate);
        return CommonResult.success();
    }


    /**
     * 清除缓存
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/clearcache")
    public CommonResult clearcache(){
        statisticsService.clearcache();
        return CommonResult.success();
    }


    private String avgTimeOfString(long sumTime,long sumSize) {
        String sumTimeString = "0";
        if (sumTime > 0 && sumSize > 0) {
            sumTime = sumTime / sumSize;
            long h = sumTime / 60 / 60 % 24;
            long m = sumTime / 60 % 60;
            sumTimeString = h + "." + m;
        }
        return sumTimeString;
    }


}
