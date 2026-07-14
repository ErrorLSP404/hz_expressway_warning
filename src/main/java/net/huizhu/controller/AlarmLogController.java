package net.huizhu.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.huizhu.common.api.CommonResult;
import net.huizhu.common.constant.RedisConstant;
import net.huizhu.common.enums.AlarmTypeEnum;
import net.huizhu.controller.dto.AlarmLogRequest;
import net.huizhu.controller.dto.AlarmLogResponse;
import net.huizhu.controller.vo.AlarmLogVo;
import net.huizhu.controller.vo.AlarmScreenshotVo;
import net.huizhu.controller.vo.AlgorithmVo;
import net.huizhu.controller.vo.CameraInfoVo;
import net.huizhu.core.entity.AlarmLog;
import net.huizhu.core.entity.AlarmScreenshot;
import net.huizhu.core.entity.CameraInfo;
import net.huizhu.core.entity.WorkOrder;
import net.huizhu.core.service.IAlarmLogService;
import net.huizhu.core.service.IAlarmScreenshotService;
import net.huizhu.core.service.IWorkOrderService;
import net.huizhu.rabbit.entity.AlarmParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/alarm")
public class AlarmLogController {

    @Autowired
    private IAlarmLogService alarmLogService;
    @Autowired
    private IAlarmScreenshotService alarmScreenshotService;
    @Autowired
    private IWorkOrderService iWorkOrderService;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 获取全部算法类型
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/getAllAlgorithm")
    public CommonResult<AlarmLogResponse> getAllAlgorithm(){
        AlarmLogResponse alarmLogResponse = new AlarmLogResponse();
        AlarmTypeEnum[] values = AlarmTypeEnum.values();
        List<AlgorithmVo> algorithmVoList = new ArrayList<>();
        for (AlarmTypeEnum alarmTypeEnum:values) {
            Integer code = alarmTypeEnum.getCode();
            String desc = alarmTypeEnum.getDesc();
            AlgorithmVo algorithmVo = new AlgorithmVo();
            algorithmVo.setCode(code);
            algorithmVo.setDesc(desc);
            algorithmVoList.add(algorithmVo);
        }
        alarmLogResponse.setAlgorithmVoList(algorithmVoList);
        return CommonResult.success(alarmLogResponse);
    }


    /**
     * 获取报警记录详细信息
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/getAlarmLog")
    public CommonResult<AlarmLogResponse> getAlarmLog(@RequestBody AlarmLogRequest alarmLogRequest){
        Long id = alarmLogRequest.getId();
        if(id == null){
            return CommonResult.failed("报警ID为空");
        }
        AlarmLogResponse alarmLogResponse = new AlarmLogResponse();
        AlarmLog alarmLog = alarmLogService.getById(id);
        if(ObjectUtil.isNotNull(alarmLog)){
            AlarmLogVo alarmLogVo = new AlarmLogVo();
            BeanUtil.copyProperties(alarmLog,alarmLogVo);
            String alarmParam = alarmLog.getAlarmParam();
            AlarmParameter alarmParameter = JSON.parseObject(alarmParam, AlarmParameter.class);
            alarmLogVo.setAlarmParameter(alarmParameter);
            QueryWrapper<AlarmScreenshot> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.lambda().eq(AlarmScreenshot::getAlarmId,alarmLog.getId());
            List<AlarmScreenshot> list = alarmScreenshotService.list(queryWrapper1);
            List<AlarmScreenshotVo> alarmScreenshotVoList = new ArrayList<>();
            if(CollUtil.isNotEmpty(list)){
                for (AlarmScreenshot alarmScreenshot:list){
                    AlarmScreenshotVo alarmScreenshotVo = new AlarmScreenshotVo();
                    BeanUtil.copyProperties(alarmScreenshot,alarmScreenshotVo);
                    alarmScreenshotVoList.add(alarmScreenshotVo);
                }
                //排序
                alarmScreenshotVoList = alarmScreenshotVoList.parallelStream().sorted(Comparator.comparing(AlarmScreenshotVo::getType).reversed()).collect(Collectors.toList());
            }
            alarmLogVo.setScreenshotList(alarmScreenshotVoList);
            alarmLogResponse.setAlarmLogVo(alarmLogVo);
        }
        return CommonResult.success(alarmLogResponse);
    }


    /**
     * 获取全部报警记录
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/getAllAlarmLog")
    public CommonResult<AlarmLogResponse> getAllAlarmLog(@RequestBody AlarmLogRequest alarmLogRequest){
        Integer pageNo = alarmLogRequest.getPageNo();
        Integer pageSize = alarmLogRequest.getPageSize();
        QueryWrapper<AlarmLog> queryWrapper = new QueryWrapper<>();
        Long highwayId = alarmLogRequest.getHighwayId();
        if(highwayId != null){
            queryWrapper.lambda().eq(AlarmLog::getHighwayId,highwayId);
        }
        Long sectionId = alarmLogRequest.getSectionId();
        if(sectionId != null){
            queryWrapper.lambda().eq(AlarmLog::getSectionId,sectionId);
        }
        Integer alarmType = alarmLogRequest.getAlarmType();
        if(alarmType != null){
            if(alarmType == 0){
                queryWrapper.lambda().eq(AlarmLog::getFalseAlarm,1);
            }else {
                queryWrapper.lambda().eq(AlarmLog::getType,alarmType);
            }
        }
        Integer status = alarmLogRequest.getStatus();
        if(status != null){
            queryWrapper.lambda().eq(AlarmLog::getStatus,status);
        }
        String startTime = alarmLogRequest.getStartTime();
        String endTime = alarmLogRequest.getEndTime();
        if(StrUtil.isNotBlank(startTime) && StrUtil.isNotBlank(endTime)){
            Date startDate = DateUtil.parse(startTime);
            Date endDate = DateUtil.parse(endTime);
            Date begin = DateUtil.beginOfDay(startDate);
            Date endOfDay = DateUtil.endOfDay(endDate);
            queryWrapper.lambda().ge(AlarmLog::getAlarmTime,LocalDateTimeUtil.of(begin)).le(AlarmLog::getAlarmTime,LocalDateTimeUtil.of(endOfDay));
        }
        queryWrapper.lambda().orderByDesc(AlarmLog::getAlarmTime);
        Page<AlarmLog> page = alarmLogService.page(new Page<>(pageNo, pageSize), queryWrapper);
        long total = page.getTotal();
        long pages = page.getPages();
        List<AlarmLog> records = page.getRecords();
        AlarmLogResponse alarmLogResponse = new AlarmLogResponse();
        alarmLogResponse.setTotalCount(total);
        alarmLogResponse.setTotalPages(pages);
        List<AlarmLogVo> alarmLogVoList = new ArrayList<>();
        if(CollUtil.isNotEmpty(records)){
            for(AlarmLog alarmLog:records){
                AlarmLogVo alarmLogVo = new AlarmLogVo();
                BeanUtil.copyProperties(alarmLog,alarmLogVo);
                String alarmParam = alarmLog.getAlarmParam();
                AlarmParameter alarmParameter = JSONUtil.toBean(alarmParam, AlarmParameter.class);
                alarmLogVo.setAlarmParameter(alarmParameter);
                QueryWrapper<AlarmScreenshot> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.lambda().eq(AlarmScreenshot::getAlarmId,alarmLog.getId());
                List<AlarmScreenshot> list = alarmScreenshotService.list(queryWrapper1);
                List<AlarmScreenshotVo> alarmScreenshotVoList = new ArrayList<>();
                if(CollUtil.isNotEmpty(list)){
                    for (AlarmScreenshot alarmScreenshot:list){
                        AlarmScreenshotVo alarmScreenshotVo = new AlarmScreenshotVo();
                        BeanUtil.copyProperties(alarmScreenshot,alarmScreenshotVo);
                        alarmScreenshotVoList.add(alarmScreenshotVo);
                    }
                    //排序
                    alarmScreenshotVoList = alarmScreenshotVoList.parallelStream().sorted(Comparator.comparing(AlarmScreenshotVo::getType).reversed()).collect(Collectors.toList());
                }
                alarmLogVo.setScreenshotList(alarmScreenshotVoList);
                alarmLogVoList.add(alarmLogVo);
            }
        }
        alarmLogResponse.setAlarmLogVoList(alarmLogVoList);
        return CommonResult.success(alarmLogResponse);
    }

    /**
     * 获取全部报警记录数据
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/getAlarmLogList")
    public CommonResult<AlarmLogResponse> getAlarmLogList(@RequestBody AlarmLogRequest alarmLogRequest){
        QueryWrapper<AlarmLog> queryWrapper = new QueryWrapper<>();
        Long highwayId = alarmLogRequest.getHighwayId();
        if(highwayId != null){
            queryWrapper.lambda().eq(AlarmLog::getHighwayId,highwayId);
        }
        Long sectionId = alarmLogRequest.getSectionId();
        if(sectionId != null){
            queryWrapper.lambda().eq(AlarmLog::getSectionId,sectionId);
        }
        Integer alarmType = alarmLogRequest.getAlarmType();
        if(alarmType != null){
            if(alarmType == 0){
                queryWrapper.lambda().eq(AlarmLog::getFalseAlarm,1);
            }else {
                queryWrapper.lambda().eq(AlarmLog::getType,alarmType);
            }
        }
        Integer status = alarmLogRequest.getStatus();
        if(status != null){
            queryWrapper.lambda().eq(AlarmLog::getStatus,status);
        }
        String startTime = alarmLogRequest.getStartTime();
        String endTime = alarmLogRequest.getEndTime();
        if(StrUtil.isNotBlank(startTime) && StrUtil.isNotBlank(endTime)){
            Date startDate = DateUtil.parse(startTime);
            Date endDate = DateUtil.parse(endTime);
            Date begin = DateUtil.beginOfDay(startDate);
            Date endOfDay = DateUtil.endOfDay(endDate);
            queryWrapper.lambda().ge(AlarmLog::getAlarmTime,LocalDateTimeUtil.of(begin)).le(AlarmLog::getAlarmTime,LocalDateTimeUtil.of(endOfDay));
        }
        queryWrapper.lambda().orderByDesc(AlarmLog::getAlarmTime);
        List<AlarmLog> alarmLogList = alarmLogService.list(queryWrapper);
        AlarmLogResponse alarmLogResponse = new AlarmLogResponse();
        List<AlarmLogVo> alarmLogVoList = new ArrayList<>();
        if(CollUtil.isNotEmpty(alarmLogList)){
            List<Long> alarmid_collect = alarmLogList.parallelStream().map(AlarmLog::getId).collect(Collectors.toList());
            QueryWrapper<AlarmScreenshot> screenshotQueryWrapper = new QueryWrapper<>();
            screenshotQueryWrapper.lambda().in(AlarmScreenshot::getAlarmId,alarmid_collect);
            List<AlarmScreenshot> alarmScreenshotList = alarmScreenshotService.list(screenshotQueryWrapper);
            if(CollUtil.isNotEmpty(alarmScreenshotList)){
                Map<Long, List<AlarmScreenshot>> alarmScreenshot_collect = alarmScreenshotList.parallelStream().collect(Collectors.groupingBy(AlarmScreenshot::getAlarmId));
                for(AlarmLog alarmLog:alarmLogList){
                    AlarmLogVo alarmLogVo = new AlarmLogVo();
                    BeanUtil.copyProperties(alarmLog,alarmLogVo);
                    String alarmParam = alarmLog.getAlarmParam();
                    AlarmParameter alarmParameter = JSONUtil.toBean(alarmParam, AlarmParameter.class);
                    alarmLogVo.setAlarmParameter(alarmParameter);
                    List<AlarmScreenshotVo> alarmScreenshotVoList = new ArrayList<>();
                    Long id = alarmLog.getId();
                    List<AlarmScreenshot> list = alarmScreenshot_collect.get(id);
                    if(CollUtil.isNotEmpty(list)){
                        for (AlarmScreenshot alarmScreenshot:list){
                            AlarmScreenshotVo alarmScreenshotVo = new AlarmScreenshotVo();
                            BeanUtil.copyProperties(alarmScreenshot,alarmScreenshotVo);
                            alarmScreenshotVoList.add(alarmScreenshotVo);
                        }
                        //排序
                        alarmScreenshotVoList = alarmScreenshotVoList.parallelStream().sorted(Comparator.comparing(AlarmScreenshotVo::getType).reversed()).collect(Collectors.toList());
                    }
                    alarmLogVo.setScreenshotList(alarmScreenshotVoList);
                    alarmLogVoList.add(alarmLogVo);
                }
            }
        }
        alarmLogResponse.setAlarmLogVoList(alarmLogVoList);
        return CommonResult.success(alarmLogResponse);
    }

    /**
     * 获取摄像头报警信息
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/getAlarmLogBycamera")
    public CommonResult<AlarmLogResponse> getAlarmLogBycamera(@RequestBody AlarmLogRequest alarmLogRequest){
        Long cameraId = alarmLogRequest.getCameraId();
        if(cameraId == null){
            return CommonResult.failed("摄像头ID为空");
        }
        Integer pageNo = alarmLogRequest.getPageNo();
        Integer pageSize = alarmLogRequest.getPageSize();
        QueryWrapper<AlarmLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ne(AlarmLog::getType,0).eq(AlarmLog::getCameraId,cameraId).orderByDesc(AlarmLog::getAlarmTime);
        Page<AlarmLog> page = alarmLogService.page(new Page<>(pageNo, pageSize), queryWrapper);
        long total = page.getTotal();
        long pages = page.getPages();
        AlarmLogResponse alarmLogResponse = new AlarmLogResponse();
        alarmLogResponse.setTotalCount(total);
        alarmLogResponse.setTotalPages(pages);
        List<AlarmLogVo> alarmLogVoList = new ArrayList<>();
        List<AlarmLog> records = page.getRecords();
        if(CollUtil.isNotEmpty(records)){
            for(AlarmLog alarmLog:records){
                AlarmLogVo alarmLogVo = new AlarmLogVo();
                BeanUtil.copyProperties(alarmLog,alarmLogVo);
                alarmLogVoList.add(alarmLogVo);
            }
        }
        alarmLogResponse.setAlarmLogVoList(alarmLogVoList);
        return CommonResult.success(alarmLogResponse);
    }


    /**
     * 获取路段报警信息(当日)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/getAlarmLogBysection")
    public CommonResult<AlarmLogResponse> getAlarmLogBysection(@RequestBody AlarmLogRequest alarmLogRequest){
        Long sectionId = alarmLogRequest.getSectionId();
        if(sectionId == null){
            return CommonResult.failed("摄像头ID为空");
        }
        Integer pageNo = alarmLogRequest.getPageNo();
        Integer pageSize = alarmLogRequest.getPageSize();
        Date now = new Date();
        DateTime beginOfDay = DateUtil.beginOfDay(now);
        DateTime endOfDay = DateUtil.endOfDay(now);
        LocalDateTime begin = LocalDateTimeUtil.of(beginOfDay);
        LocalDateTime end = LocalDateTimeUtil.of(endOfDay);
        QueryWrapper<AlarmLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ne(AlarmLog::getType,0).eq(AlarmLog::getSectionId,sectionId)
                .ge(AlarmLog::getAlarmTime,begin).le(AlarmLog::getAlarmTime,end)
                .orderByDesc(AlarmLog::getAlarmTime);
        Page<AlarmLog> page = alarmLogService.page(new Page<>(pageNo, pageSize), queryWrapper);
        long total = page.getTotal();
        long pages = page.getPages();
        AlarmLogResponse alarmLogResponse = new AlarmLogResponse();
        alarmLogResponse.setTotalCount(total);
        alarmLogResponse.setTotalPages(pages);
        List<AlarmLogVo> alarmLogVoList = new ArrayList<>();
        List<AlarmLog> records = page.getRecords();
        if(CollUtil.isNotEmpty(records)){
            for(AlarmLog alarmLog:records){
                AlarmLogVo alarmLogVo = new AlarmLogVo();
                BeanUtil.copyProperties(alarmLog,alarmLogVo);
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
            }
        }
        alarmLogResponse.setAlarmLogVoList(alarmLogVoList);
        return CommonResult.success(alarmLogResponse);
    }


    /**
     * 获取公路报警信息
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/getAlarmLogByhighway")
    public CommonResult<AlarmLogResponse> getAlarmLogByhighway(@RequestBody AlarmLogRequest alarmLogRequest){
        Long highwayId = alarmLogRequest.getHighwayId();
        if(highwayId == null){
            return CommonResult.failed("摄像头ID为空");
        }
        Integer pageNo = alarmLogRequest.getPageNo();
        Integer pageSize = alarmLogRequest.getPageSize();
        QueryWrapper<AlarmLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ne(AlarmLog::getType,0).eq(AlarmLog::getHighwayId,highwayId).orderByDesc(AlarmLog::getAlarmTime);
        Page<AlarmLog> page = alarmLogService.page(new Page<>(pageNo, pageSize), queryWrapper);
        long total = page.getTotal();
        long pages = page.getPages();
        AlarmLogResponse alarmLogResponse = new AlarmLogResponse();
        alarmLogResponse.setTotalCount(total);
        alarmLogResponse.setTotalPages(pages);
        List<AlarmLogVo> alarmLogVoList = new ArrayList<>();
        List<AlarmLog> records = page.getRecords();
        if(CollUtil.isNotEmpty(records)){
            for(AlarmLog alarmLog:records){
                AlarmLogVo alarmLogVo = new AlarmLogVo();
                BeanUtil.copyProperties(alarmLog,alarmLogVo);
                alarmLogVoList.add(alarmLogVo);
            }
        }
        alarmLogResponse.setAlarmLogVoList(alarmLogVoList);
        return CommonResult.success(alarmLogResponse);
    }

    /**
     * 获取算法报警信息
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/getAlarmLogByAlgorithm")
    public CommonResult<AlarmLogResponse> getAlarmLogByAlgorithm(@RequestBody AlarmLogRequest alarmLogRequest){
        Integer algorithmNum = alarmLogRequest.getAlgorithmNum();
        if(algorithmNum == null){
            return CommonResult.failed("算法编号为空");
        }
        Integer pageNo = alarmLogRequest.getPageNo();
        Integer pageSize = alarmLogRequest.getPageSize();
        QueryWrapper<AlarmLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ne(AlarmLog::getType,0).eq(AlarmLog::getAlgorithmNum,algorithmNum).orderByDesc(AlarmLog::getAlarmTime);
        Page<AlarmLog> page = alarmLogService.page(new Page<>(pageNo, pageSize), queryWrapper);
        long total = page.getTotal();
        long pages = page.getPages();
        AlarmLogResponse alarmLogResponse = new AlarmLogResponse();
        alarmLogResponse.setTotalCount(total);
        alarmLogResponse.setTotalPages(pages);
        List<AlarmLogVo> alarmLogVoList = new ArrayList<>();
        List<AlarmLog> records = page.getRecords();
        if(CollUtil.isNotEmpty(records)){
            for(AlarmLog alarmLog:records){
                AlarmLogVo alarmLogVo = new AlarmLogVo();
                BeanUtil.copyProperties(alarmLog,alarmLogVo);
                alarmLogVoList.add(alarmLogVo);
            }
        }
        alarmLogResponse.setAlarmLogVoList(alarmLogVoList);
        return CommonResult.success(alarmLogResponse);
    }

    /**
     * 核验报警
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/verificationalarm")
    public CommonResult<AlarmLogResponse> verificationalarm(@RequestBody AlarmLogRequest alarmLogRequest){
        Long id = alarmLogRequest.getId();
        if(id == null){
            return CommonResult.failed("报警ID为空");
        }
        Integer falseAlarm = alarmLogRequest.getFalseAlarm();
        String remarks = alarmLogRequest.getRemarks();
        AlarmLog alarmLog = alarmLogService.getById(id);
        if(ObjectUtil.isNotNull(alarmLog)){
            Integer alarmType = alarmLogRequest.getAlarmType();
            alarmLog.setType(alarmType);
            if(falseAlarm == 1){
                alarmLog.setFalseAlarm(falseAlarm);
            }
            if(StrUtil.isNotBlank(remarks)){
                alarmLog.setRemarks(remarks);
            }
            alarmLog.setStatus(1);
            boolean update = alarmLogService.updateById(alarmLog);

            // 修改工单
            QueryWrapper<WorkOrder> workOrderQueryWrapper = new QueryWrapper<>();
            workOrderQueryWrapper.lambda().eq(WorkOrder::getAId,id);
            WorkOrder workOrder = iWorkOrderService.getOne(workOrderQueryWrapper);
            if (ObjectUtil.isNotNull(workOrder)) {
                workOrder.setAlarmStatus(1);
                iWorkOrderService.updateById(workOrder);
            }

            if(update){
                return CommonResult.success();
            }
        }
        return CommonResult.failed();
    }

    /**
     * 批量核验
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/batchVerificationalarm")
    public CommonResult<AlarmLogResponse> batchVerificationalarm(@RequestBody AlarmLogRequest alarmLogRequest){
        List<Long> alarmLogIds = alarmLogRequest.getAlarmLogIds();
        if (CollUtil.isEmpty(alarmLogIds)) {
            return CommonResult.failed("报警Id不能为空");
        }
        Integer falseAlarm = alarmLogRequest.getFalseAlarm();
        String remarks = alarmLogRequest.getRemarks();
        Integer alarmType = alarmLogRequest.getAlarmType();

        List<AlarmLog> alarmLogList = alarmLogService.listByIds(alarmLogIds);
        if (CollUtil.isNotEmpty(alarmLogList)) {

            alarmLogList.forEach(alarmLog -> {
                alarmLog.setType(alarmType);
                if(falseAlarm == 1){
                    alarmLog.setFalseAlarm(falseAlarm);
                }
                if(StrUtil.isNotBlank(remarks)){
                    alarmLog.setRemarks(remarks);
                }
                alarmLog.setStatus(1);
            });
            alarmLogService.updateBatchById(alarmLogList);

            // 修改工单
            QueryWrapper<WorkOrder> workOrderQueryWrapper = new QueryWrapper<>();
            workOrderQueryWrapper.lambda().in(WorkOrder::getAId,alarmLogIds);
            List<WorkOrder> list = iWorkOrderService.list(workOrderQueryWrapper);
            if (CollUtil.isNotEmpty(list)) {
                list.forEach(workOrder -> {
                    workOrder.setAlarmStatus(1);
                });
            }
            iWorkOrderService.updateBatchById(list);
        } else {
            return CommonResult.failed();
        }
        return CommonResult.success();
    }

    /**
     * 获取所有待核验摄像头
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/notverificationvcamera")
    public CommonResult<AlarmLogResponse> notverificationvcamera(){
        Date now = new Date();
        DateTime beginOfDay = DateUtil.beginOfDay(now);
        DateTime endOfDay = DateUtil.endOfDay(now);
        LocalDateTime begin = LocalDateTimeUtil.of(beginOfDay);
        LocalDateTime end = LocalDateTimeUtil.of(endOfDay);
        QueryWrapper<AlarmLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AlarmLog::getStatus,0)
                .ge(AlarmLog::getAlarmTime,begin).le(AlarmLog::getAlarmTime,end)
                .orderByDesc(AlarmLog::getAlarmTime);
        List<AlarmLog> list = alarmLogService.list(queryWrapper);
        AlarmLogResponse alarmLogResponse = new AlarmLogResponse();
        List<CameraInfoVo> cameraInfoVoList = new ArrayList<>();
        if(CollUtil.isNotEmpty(list)){
            for (AlarmLog alarmLog:list) {
                CameraInfoVo cameraInfoVo = new CameraInfoVo();
                String cameraId = String.valueOf(alarmLog.getCameraSerialNumber());
                String json = (String) redisTemplate.opsForHash().get(RedisConstant.CAMERA, cameraId);
                CameraInfo cameraInfo = JSONUtil.toBean(json, CameraInfo.class);
                BeanUtil.copyProperties(cameraInfo,cameraInfoVo);
                String cameraMapCoordinate = cameraInfo.getCameraMapCoordinate();
                if(StrUtil.isNotBlank(cameraMapCoordinate)){
                    String longitude = StrUtil.subBefore(cameraMapCoordinate, "#", false);
                    String latitude = StrUtil.subAfter(cameraMapCoordinate,"#",false);
                    //经度
                    cameraInfoVo.setLongitude(longitude);
                    //纬度
                    cameraInfoVo.setLatitude(latitude);
                }
                cameraInfoVoList.add(cameraInfoVo);
            }
        }
        alarmLogResponse.setCameraInfoVoList(cameraInfoVoList);
        return CommonResult.success(alarmLogResponse);
    }

}
