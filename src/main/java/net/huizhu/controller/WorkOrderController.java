package net.huizhu.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import net.huizhu.common.api.CommonResult;
import net.huizhu.common.config.RabbitConfig;
import net.huizhu.common.config.UploadConfig;
import net.huizhu.common.enums.*;
import net.huizhu.common.util.JWTTokenUtil;
import net.huizhu.common.util.NOUtil;
import net.huizhu.common.util.UploadUtil;
import net.huizhu.controller.dto.WorkOrderRequest;
import net.huizhu.controller.dto.WorkOrderResponse;
import net.huizhu.controller.vo.CameraAndMessageVo;
import net.huizhu.controller.vo.WorkOrderVo;
import net.huizhu.core.entity.*;
import net.huizhu.core.service.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/workOrder")
public class WorkOrderController {

    @Autowired
    private IDeptService iDeptService;
    @Autowired
    private IOfficeService iOfficeService;
    @Autowired
    private IWorkOrderService iWorkOrderService;
    @Autowired
    private IWorkOrderLogService iWorkOrderLogService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ISysUserRoleService iSysUserRoleService;
    @Autowired
    private ISysUserDeptService iSysUserDeptService;
    @Autowired
    private ISysUserOffService iSysUserOffService;
    @Autowired
    private IWorkImageService workImageService;
    @Autowired
    private IAlarmLogService iAlarmLogService;
    @Autowired
    private IWorkImageService iWorkImageService;
    @Autowired
    private IHighwayService iHighwayService;
    @Autowired
    private ICameraInfoService cameraInfoService;
    @Autowired
    private IAlarmScreenshotService iAlarmScreenshotService;


    /**
     * 添加工单
     */
    @PreAuthorize("hasAnyRole('ADMIN','DEPT')")
    @PostMapping(value = "/addWorkOrder")
    public CommonResult<WorkOrderResponse> addWorkOrder(@RequestHeader("Authorization") String token,@RequestBody WorkOrderRequest workOrderRequest){
        // 获取用户id 用于返回不同的页面
        Claims claims = JWTTokenUtil.getClaims(token);
        Long userId = Long.valueOf(claims.getId());
        QueryWrapper<SysUserRole> sysUserRoleQueryWrapper = new QueryWrapper<>();
        sysUserRoleQueryWrapper.lambda().eq(SysUserRole::getUserId,userId);
        SysUserRole sysUserRole = iSysUserRoleService.getOne(sysUserRoleQueryWrapper);
        Integer options = null;
        if (sysUserRole.getRoleId().equals(RoleEnum.ADMIN.getCode())) {
            options = 0;
        } else if (sysUserRole.getRoleId().equals(RoleEnum.DEPT.getCode())) {
            options = 1;
        }
        if (options == null) {
            return CommonResult.failed("用户角色获取异常");
        }
        // 部门或科室Id
        Long deptId = workOrderRequest.getDeptId();

        String remark = workOrderRequest.getRemark();

        List<Long> alarmIds = workOrderRequest.getAlarmIds();
        List<Long> wordOrderIds = workOrderRequest.getWordOrderIds();

        if (deptId == null) {
            return CommonResult.failed("部门Id不能为空");
        }

        List<WorkOrder> workOrderList = null;
        QueryWrapper<WorkOrder> workOrderQueryWrapper = new QueryWrapper<>();
        if (CollUtil.isNotEmpty(alarmIds)) {
            workOrderQueryWrapper.lambda().in(WorkOrder::getAId,alarmIds);
            workOrderList = iWorkOrderService.list(workOrderQueryWrapper);
        } else if (CollUtil.isNotEmpty(wordOrderIds)){
            workOrderQueryWrapper.lambda().in(WorkOrder::getId,wordOrderIds);
            workOrderList = iWorkOrderService.list(workOrderQueryWrapper);
        }
        if (workOrderList != null) {
            for (WorkOrder workOrder:workOrderList) {
                // 日志
                WorkOrderLog workOrderLog = new WorkOrderLog();
                Long workNo = workOrder.getWorkNo();
                workOrderLog.setWorkNo(workNo);
                workOrderLog.setType(workOrder.getType());
                workOrderLog.setCameraLocation(workOrder.getCameraLocation());
                switch (options) {
                    case 0: // 超级管理员派单
                        String timeOut = workOrderRequest.getTimeOut();
                        if (StrUtil.isBlank(timeOut)) {
                            return CommonResult.failed("时限不能为空");
                        }
                        int timeOutToLong = Integer.parseInt(timeOut);
                        // 获取部门详情
                        if (workOrder.getStatus().equals(WorkOrderEnum.STATUS.getCode())) {
                            Dept dept = iDeptService.getById(deptId);
                            if (dept != null) {  // 只有超级管理员下单的时候能设置时限
                                LocalDateTime now = LocalDateTime.now();
                                workOrder.setDId(deptId);
                                workOrder.setDName(dept.getName());
                                workOrder.setTimePeriod(now.plusHours(timeOutToLong));
                                workOrder.setGmtModified(now);
                                workOrder.setOrderTime(now);
                                workOrder.setStatus(WorkOrderEnum.PROCESSING.getCode());
                                workOrder.setTimeOutStr(timeOut);
                                if (StrUtil.isNotBlank(remark)) {
                                    workOrder.setRemark(remark);
                                }
                                workOrder.setRevocation(RevocationEnum.UN_REPEALED.getCode());
                            } else {
                                return CommonResult.failed("部门不存在");
                            }
                            // 更新节点状态
                            iWorkOrderLogService.updateNode(workNo);
                            workOrderLog.setNode(WorkNodeEnum.DEPT_NODE.getCode());
                            if (StrUtil.isNotBlank(workOrder.getDName())) {
                                workOrderLog.setDName(workOrder.getDName());
                            }
                            // 更新报警状态
                            AlarmLog alarmLog = iAlarmLogService.getById(workOrder.getAId());
                            alarmLog.setWorderStatus(WorkOrderEnum.PROCESSING.getCode());
                            iAlarmLogService.updateById(alarmLog);

                            //  发送延迟消息  用于效验超时
                            MessageProperties properties = new MessageProperties();
                            properties.setDelay(((timeOutToLong * 1000) * 60) * 60);      //设置延时时间
                            System.out.println("时间" + new Date());
                            Message msg = new Message(workOrder.getWorkNo().toString().getBytes(StandardCharsets.UTF_8),properties);
                            rabbitTemplate.convertAndSend(RabbitConfig.DELAYED_EXCHANGE_NAME,RabbitConfig.DELAYED_ROUTING_KEY,msg);
                        } else {
                            return CommonResult.failed("派单失败，工单已被派单！");
                        }
                        break;
                    case 1:  // 部门派单
                        // 派单部门判断是否超时
                        if (workOrder.getOId() != null && !workOrder.getOId().equals(-1L)) {
                            return CommonResult.failed("派单失败，工单已被派单");
                        }

                        if (workOrder.getTimeOut().equals(TimeOutEnum.TIMEOUT_NO.getCode())) {
                            // 部门派单判断是否撤销
                            if (workOrder.getRevocation().equals(RevocationEnum.UN_REPEALED.getCode())) {
                                Office office = iOfficeService.getById(deptId);
                                if (office != null) {
                                    workOrder.setOId(deptId);
                                    workOrder.setOName(office.getName());
                                    workOrder.setGmtModified(LocalDateTime.now());
                                    if (StrUtil.isNotBlank(remark)) {
                                        workOrder.setRemarkDept(remark);
                                    }
                                } else {
                                    return CommonResult.failed("科室不存在");
                                }
                                // 更新节点状态
                                iWorkOrderLogService.updateNode(workNo);
                                workOrderLog.setNode(WorkNodeEnum.OFFICE_NODE.getCode());
                                if (StrUtil.isNotBlank(workOrder.getOName())) {
                                    workOrderLog.setDName(workOrder.getOName());
                                }
                            } else {
                                return CommonResult.failed("派单失败，此工单已被撤销");
                            }
                        } else {
                            return CommonResult.failed("派单失败，工单已超时");
                        }
                    break;
                }
                // 添加日志
                iWorkOrderLogService.save(workOrderLog);
                // 更新
                iWorkOrderService.updateById(workOrder);
            }
        }
        return CommonResult.success();
    }

    /**
     *  获取工单列表
     */
    @PreAuthorize("hasAnyRole('ADMIN','DEPT','OFFICE')")
    @PostMapping(value = "/getWorkOrderList")
    public CommonResult<WorkOrderResponse> getWorkOrderList(@RequestHeader("Authorization") String token,@RequestBody WorkOrderRequest workOrderRequest){

        Integer type = workOrderRequest.getType(); // 报警类型 0:无事件发生 1:交通事件2:路面异物3:公路标志标线损坏4:安防设施损坏5:非法占用公路行为
        Integer status = workOrderRequest.getStatus();
        Long deptId = workOrderRequest.getDeptId();
        Long officeId = workOrderRequest.getOfficeId();
        String startDateTime = workOrderRequest.getStartDateTime();
        String endDateTime = workOrderRequest.getEndDateTime();
        Long highwayId = workOrderRequest.getHighwayId();
        Long sectionId = workOrderRequest.getSectionId();
        Integer page = workOrderRequest.getPage();
        Integer pageSize = workOrderRequest.getPageSize();
        LocalDateTime dateTimeStart = null;
        LocalDateTime dateTimeEnd = null;
        if (StrUtil.isNotBlank(startDateTime)) {
            dateTimeStart = LocalDate.
                    parse(startDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(0,0,0);
        }
        if (StrUtil.isNotBlank(endDateTime)) {
            dateTimeEnd = LocalDate.
                    parse(endDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(23,59,59);
        }

        WorkOrderResponse workOrderResponse = new WorkOrderResponse();
        // 封装
        List<WorkOrderVo> workOrderVoList = new ArrayList<>();    
        QueryWrapper<WorkOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(WorkOrder::getAlarmStatus,1);
        //queryWrapper.lambda().orderByAsc(WorkOrder::getStatus);
        queryWrapper.lambda().orderBy(true,true,WorkOrder::getStatus).orderBy(true,true,WorkOrder::getTimeOut);
        if (status != null) {
            queryWrapper.lambda().eq(WorkOrder::getStatus,status);
        }
        if (highwayId != null) {
            queryWrapper.lambda().eq(WorkOrder::getHighwayId,highwayId);
        }
        if (sectionId != null) {
            queryWrapper.lambda().eq(WorkOrder::getSectionId,sectionId);
        }
        if (type != null) {
            queryWrapper.lambda().eq(WorkOrder::getType,type);
        }
        if (dateTimeStart != null && dateTimeEnd != null) {
            queryWrapper.lambda().between(WorkOrder::getGmtCreate,dateTimeStart,dateTimeEnd);
        }else {
            if (dateTimeStart != null) {
                queryWrapper.lambda().ge(WorkOrder::getGmtCreate,dateTimeStart);
            }
            if (dateTimeEnd != null) {
                queryWrapper.lambda().le(WorkOrder::getGmtCreate,dateTimeEnd);
            }
        }
        queryWrapper.lambda().orderBy(true,false,WorkOrder::getGmtCreate);
        // 获取用户id 用于返回不同的页面
        Claims claims = JWTTokenUtil.getClaims(token);
        Long userId = Long.valueOf(claims.getId());

        // 获取角色信息
        QueryWrapper<SysUserRole> sysUserRoleQueryWrapper = new QueryWrapper<>();
        sysUserRoleQueryWrapper.lambda().eq(SysUserRole::getUserId,userId);
        SysUserRole sysUserRole = iSysUserRoleService.getOne(sysUserRoleQueryWrapper);
        if (sysUserRole != null) {
            if (sysUserRole.getRoleId().equals(RoleEnum.ADMIN.getCode())) {
                if (deptId != null) {
                    queryWrapper.lambda().eq(WorkOrder::getDId, deptId);
                }
                if (officeId != null) {
                    queryWrapper.lambda().eq(WorkOrder::getOId, officeId);
                }
            }else if (sysUserRole.getRoleId().equals(RoleEnum.DEPT.getCode())) {
                QueryWrapper<SysUserDept> sysUserDeptQueryWrapper = new QueryWrapper<>();
                sysUserDeptQueryWrapper.lambda().eq(SysUserDept::getUId,userId);
                SysUserDept sysUserDept = iSysUserDeptService.getOne(sysUserDeptQueryWrapper);
                if (sysUserDept != null) {
                    queryWrapper.lambda().eq(WorkOrder::getDId,sysUserDept.getDId());
                } else {
                    return CommonResult.failed("部门数据异常");
                }
            } else if (sysUserRole.getRoleId().equals(RoleEnum.OFFICE.getCode())) {
                QueryWrapper<SysUserOff> sysUserOffQueryWrapper = new QueryWrapper<>();
                sysUserOffQueryWrapper.lambda().eq(SysUserOff::getUId,userId);
                SysUserOff sysUserOff = iSysUserOffService.getOne(sysUserOffQueryWrapper);
                if (sysUserOff != null) {
                    queryWrapper.lambda().eq(WorkOrder::getOId,sysUserOff.getOId());
                } else {
                    return CommonResult.failed("科室数据异常");
                }

            }
        }

        Page<WorkOrder> workOrderPage =
                iWorkOrderService.page(new Page<>(page, pageSize), queryWrapper);
        if (workOrderPage != null) {
            List<WorkOrder> records = workOrderPage.getRecords();
            records.forEach(workOrder -> {
                WorkOrderVo workOrderVo = new WorkOrderVo();
                String timeOutToString = "";
                if (workOrder.getStatus().equals(WorkOrderEnum.FINISH.getCode())) {
                    LocalDateTime gmtModified = workOrder.getGmtModified();
                    LocalDateTime orderTime = workOrder.getOrderTime();
                    if (orderTime != null && workOrder.getQuickFinish().equals(WorkQuickEnum.SLOW.getCode())) { // 这里判断的是是否直接结办 直接结办没有下单时间 ，因此时限给空字符串
                        timeOutToString = getTimeOutToString(gmtModified, orderTime)
                                + " / " + workOrder.getTimeOutStr()+"时";
                    }
                } else if (workOrder.getTimeOut().equals(TimeOutEnum.TIMEOUT_YES.getCode())) {
                    timeOutToString = workOrder.getTimeOutStr() + " / " + workOrder.getTimeOutStr()+"时";
                } else if (workOrder.getStatus().equals(WorkOrderEnum.PROCESSING.getCode())) {

                    LocalDateTime startTime = workOrder.getOrderTime();
                    LocalDateTime endTime = LocalDateTime.now();
                    timeOutToString = getTimeOutToString(startTime, endTime)
                            + " / " + workOrder.getTimeOutStr()+"时";
                }
                BeanUtil.copyProperties(workOrder, workOrderVo);
                workOrderVo.setStartDataTime(DateUtil.format(workOrder.getGmtCreate(),"yyyy-MM-dd HH:mm:ss"));
                workOrderVo.setTimeInfo(timeOutToString);
                // 获取工单图片
                QueryWrapper<WorkImage> workImageQueryWrapper = new QueryWrapper<>();
                workImageQueryWrapper.lambda().eq(WorkImage::getWorkNo,workOrder.getWorkNo());
                List<WorkImage> workImages = iWorkImageService.list(workImageQueryWrapper);
                workImages.forEach(workImage -> {
                    workImage.setWorkImage(UploadConfig.fileUrl + workImage.getWorkImage());
                });
                if (CollUtil.isEmpty(workImages)) {
                    workImages = new ArrayList<>();
                }
                workOrderVo.setWorkImage(workImages);

                // 获取报警信息
                AlarmLog alarmLog = iAlarmLogService.getById(workOrder.getAId());
                if (alarmLog != null) {
                    workOrderVo.setContent(alarmLog.getContent());
                    CameraInfo cameraInfo = cameraInfoService.getById(alarmLog.getCameraId());
                    if (cameraInfo != null) {
                        String cameraMapCoordinate = cameraInfo.getCameraMapCoordinate();
                        String longitude = StrUtil.subBefore(cameraMapCoordinate, "#", false);
                        String latitude = StrUtil.subAfter(cameraMapCoordinate,"#",false);
                        CameraAndMessageVo cameraAndMessageVo = new CameraAndMessageVo();
                        cameraAndMessageVo.setLongitude(longitude);
                        cameraAndMessageVo.setLatitude(latitude);
                        cameraAndMessageVo.setCameraLocation(cameraInfo.getCameraLocation());
                        // 获取报警图片
                        QueryWrapper<AlarmScreenshot> alarmScreenshotQueryWrapper = new QueryWrapper<>();
                        alarmScreenshotQueryWrapper.lambda().eq(AlarmScreenshot::getAlarmId,alarmLog.getId());
                        List<AlarmScreenshot> alarmScreenshotList = iAlarmScreenshotService.list(alarmScreenshotQueryWrapper);
                        if (CollUtil.isNotEmpty(alarmScreenshotList)) {
                            cameraAndMessageVo.setAlarmScreenshotList(alarmScreenshotList);
                        }
                        workOrderVo.setCameraAndMessageVo(cameraAndMessageVo);
                    }
                }

                // 获取工单节点信息
                QueryWrapper<WorkOrderLog> workOrderLogQueryWrapper = new QueryWrapper<>();
                workOrderLogQueryWrapper.lambda().eq(WorkOrderLog::getWorkNo,workOrder.getWorkNo());
                List<WorkOrderLog> list = iWorkOrderLogService.list(workOrderLogQueryWrapper);
                if (CollUtil.isNotEmpty(list)) {
                    list.forEach(workOrderLog -> {
                        Integer node = workOrderLog.getNode();
                        switch (node) {
                            case 1:
                                workOrderVo.setAdminSendOrderTime(DateUtil.
                                        format(workOrderLog.getGmtCreate(),"yyyy-MM-dd HH:mm:ss"));
                                break;
                            case 2:
                                workOrderVo.setDeptSendOrderTime(DateUtil.
                                        format(workOrderLog.getGmtCreate(),"yyyy-MM-dd HH:mm:ss"));
                                break;
                            case 3:
                                workOrderVo.setOfficeSendOrderTime(DateUtil.
                                        format(workOrderLog.getGmtCreate(),"yyyy-MM-dd HH:mm:ss"));
                                break;
                        }
                    });
                    workOrderVo.setNode(list.size());
                }
                workOrderVoList.add(workOrderVo);
            });

            workOrderResponse.setWorkOrderVoList(workOrderVoList);
            workOrderResponse.setTotal(workOrderPage.getTotal());
            return CommonResult.success(workOrderResponse);
        } else {
            return CommonResult.failed("数据获取异常");
        }

    }

    /**
     * 撤销功能
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/revocation")
    public CommonResult<WorkOrderResponse> adminRevocation(@RequestBody WorkOrderRequest workOrderRequest){
        Long wordOrderId = workOrderRequest.getWordOrderId();
        if (wordOrderId == null) {
            return CommonResult.failed("工单Id不能为空");
        }
        WorkOrder workOrder = iWorkOrderService.getById(wordOrderId);
        if (workOrder != null) {
                Integer status = workOrder.getStatus();
                if (!status.equals(WorkOrderEnum.FINISH.getCode())) {
                    if (!status.equals(WorkOrderEnum.STATUS.getCode())) {
                        Long workOrderNo = NOUtil.getWorkOrderNo();
                        QueryWrapper<WorkOrderLog> workOrderLogQueryWrapper = new QueryWrapper<>();
                        workOrderLogQueryWrapper.lambda().eq(WorkOrderLog::getWorkNo, workOrder.getWorkNo());
                        List<WorkOrderLog> list = iWorkOrderLogService.list(workOrderLogQueryWrapper);
                        list.forEach(workOrderLog -> {
                            workOrderLog.setWorkNo(workOrderNo);
                            iWorkOrderLogService.updateById(workOrderLog);
                        });

                        if (workOrder.getDId() != null) {
                            workOrder.setDId(-1L);
                            workOrder.setDName("");
                        }
                        if (workOrder.getOId() != null) {
                            workOrder.setOId(-1L);
                            workOrder.setOName("");
                        }
                        workOrder.setStatus(WorkOrderEnum.STATUS.getCode());
                        workOrder.setRevocation(1);
                        workOrder.setTimeOut(TimeOutEnum.TIMEOUT_NO.getCode());
                        workOrder.setTimeOutStr("");
                        workOrder.setRemark("");
                        workOrder.setGmtModified(LocalDateTime.now());
                        workOrder.setWorkNo(workOrderNo);
                        iWorkOrderService.updateById(workOrder);
                        AlarmLog alarmLog = iAlarmLogService.getById(workOrder.getAId());
                        alarmLog.setWorderStatus(WorkOrderEnum.STATUS.getCode());
                        iAlarmLogService.updateById(alarmLog);

                        QueryWrapper<WorkOrderLog> workOrderLogQueryWrapperOne = new QueryWrapper<>();
                        workOrderLogQueryWrapperOne.lambda().eq(WorkOrderLog::getWorkNo, workOrderNo);
                        workOrderLogQueryWrapperOne.lambda().gt(WorkOrderLog::getNode, WorkNodeEnum.ADMIN_NODE.getCode());
                        iWorkOrderLogService.remove(workOrderLogQueryWrapperOne);
                        QueryWrapper<WorkOrderLog> workOrderLogQueryWrapperTwo = new QueryWrapper<>();
                        workOrderLogQueryWrapperTwo.lambda().eq(WorkOrderLog::getWorkNo, workOrderNo);
                        WorkOrderLog workOrderLog = iWorkOrderLogService.getOne(workOrderLogQueryWrapperTwo);
                        if (workOrderLog != null) {
                            workOrderLog.setLastNode(1);
                            iWorkOrderLogService.updateById(workOrderLog);
                        }else {
                            return CommonResult.failed("节点处理异常");
                        }

                        return CommonResult.success();
                    }else {
                        return CommonResult.failed("撤销失败，工单未派单");
                    }
                } else {
                    return CommonResult.failed("撤销失败，工单已结办");
                }
        } else {
            return CommonResult.failed("工单获取失败");
        }

    }

    /**
     * 直接结办
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/quickFinish")
    public CommonResult<WorkOrderResponse> quickFinish(@RequestBody WorkOrderRequest workOrderRequest){
        List<Long> wordOrderIdList = workOrderRequest.getWordOrderIdList();
        List<Long> alarmIds = workOrderRequest.getAlarmIds();
        String remark = workOrderRequest.getRemark();

        List<WorkOrder> workOrders;
        if (CollUtil.isNotEmpty(wordOrderIdList)) {
            workOrders = iWorkOrderService.listByIds(wordOrderIdList);
        } else if (CollUtil.isNotEmpty(alarmIds)) {
            QueryWrapper<WorkOrder> workOrderQueryWrapper = new QueryWrapper<>();
            workOrderQueryWrapper.lambda().in(WorkOrder::getAId,alarmIds);
            workOrders = iWorkOrderService.list(workOrderQueryWrapper);
        } else {
            return CommonResult.failed("缺少核心参数");
        }

        if (CollUtil.isNotEmpty(workOrders)) {
            for (WorkOrder workOrder:workOrders) {
                if (workOrder.getStatus().equals(WorkOrderEnum.STATUS.getCode())) {
                    if (StrUtil.isNotBlank(remark)) {
                        workOrder.setRemark(remark);
                    }
                    workOrder.setStatus(WorkOrderEnum.FINISH.getCode());
                    workOrder.setQuickFinish(WorkQuickEnum.QUICK.getCode());
                    workOrder.setGmtModified(LocalDateTime.now());
                    iWorkOrderService.updateById(workOrder);
                    AlarmLog alarmLog = iAlarmLogService.getById(workOrder.getAId());
                    alarmLog.setWorderStatus(WorkOrderEnum.FINISH.getCode());
                    iAlarmLogService.updateById(alarmLog);
                }
            }
        }else {
            return CommonResult.failed("工单不存在");
        }
        return CommonResult.success();
    }

    /**
     * 科室结办
     */

    @PreAuthorize("hasAnyRole('DEPT','OFFICE')")
    @PostMapping(value = "/officeFinish")
    public CommonResult<WorkOrderResponse> officeFinish(@RequestParam(value = "file",required = false) MultipartFile[] file,
                                                        Long wordOrderId,
                                                        String remarkOffice){

        if (wordOrderId == null) {
            return CommonResult.failed("工单id不能为空");
        }

        WorkOrder workOrder = iWorkOrderService.getById(wordOrderId);

        if (file != null) {
            if (file.length > 5) {
                return CommonResult.failed("最对只允许上传5张图片");
            }
            WorkImage workImage = new WorkImage();
            // 图片上传
            for (MultipartFile file1 : file) {
                // 判断后缀
                String ext = FilenameUtils.getExtension(file1.getOriginalFilename()).toLowerCase();
                if (UploadUtil.extlsit.contains(ext)) {
                    String url = UploadUtil.upload("expressway", file1);
                    if (url.equals("0")) {
                        log.info("文件上传失败");
                    } else {
                        log.info("上传成功，文件地址：" + UploadConfig.fileUrl + "expressway/" + url);
                    }
                    workImage.setWorkNo(workOrder.getWorkNo());
                    workImage.setWorkImage(url);
                    workImageService.save(workImage);
                }
            }
        }

        if (workOrder != null) {
            if (workOrder.getStatus().equals(WorkOrderEnum.PROCESSING.getCode())) {
                //if (workOrder.getTimeOut().equals(TimeOutEnum.TIMEOUT_NO.getCode())) {  // 修改超时也可以办结
                    if (workOrder.getRevocation().equals(RevocationEnum.UN_REPEALED.getCode())) {
                        if (StrUtil.isNotBlank(remarkOffice)) {
                            workOrder.setRemarkOffice(remarkOffice);
                        }
                        workOrder.setStatus(WorkOrderEnum.FINISH.getCode()); // 结办
                        workOrder.setGmtModified(LocalDateTime.now());
                        iWorkOrderService.updateById(workOrder);
                        AlarmLog alarmLog = iAlarmLogService.getById(workOrder.getAId());
                        alarmLog.setWorderStatus(WorkOrderEnum.FINISH.getCode());
                        iAlarmLogService.updateById(alarmLog);
                    }
                //}
            }
        } else {
            return CommonResult.failed("工单数据获取异常");
        }
        return CommonResult.success();
    }


    private String getTimeOutToString(LocalDateTime startTime,LocalDateTime endTime) {
        //获取秒数
        long nowSecond = startTime.toEpochSecond(ZoneOffset.ofHours(0));
        long endSecond = endTime.toEpochSecond(ZoneOffset.ofHours(0));
        long absSeconds = Math.abs(nowSecond - endSecond);

        StringBuilder stringBuilder = new StringBuilder();

        //获取天数
        long d = absSeconds / 60 / 60 / 24;
        if (d != 0) {
            stringBuilder.append(d).append("天");
        }
        //获取小时数
        long h = absSeconds / 60 / 60 % 24;
        if (h != 0) {
            stringBuilder.append(h).append("时");
        }
        //获取分钟数
        long m = absSeconds / 60 % 60;
        if (m != 0) {
            stringBuilder.append(m).append("分");
        }
        //获取秒数
        //long s = absSeconds % 60;

        String str = stringBuilder.toString();
        if (StrUtil.isBlank(str)) {
            return "0分";
        }
        return str;
    }


    /**
     * 初始化工单数据
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/initWorkData")
    public CommonResult<WorkOrderResponse> initWorkData(){
        // 获取报警数据
        List<AlarmLog> alarmLogList = iAlarmLogService.list();
        alarmLogList.forEach(alarmLog -> {
            ThreadUtil.sleep(1);
            // 工单
            WorkOrder workOrder = new WorkOrder();
            Long workOrderNo = NOUtil.getWorkOrderNo();
            Highway highway = iHighwayService.getById(alarmLog.getHighwayId());
            workOrder.setHighwayName(highway.getHighwayName());
            workOrder.setCameraLocation(alarmLog.getCameraLocation());
            workOrder.setHighwayId(alarmLog.getHighwayId());
            workOrder.setSectionId(alarmLog.getSectionId());
            workOrder.setAId(alarmLog.getId());
            workOrder.setType(alarmLog.getType());
            workOrder.setWorkNo(workOrderNo);
            iWorkOrderService.save(workOrder);
            // 工单记录
            WorkOrderLog workOrderLog = new WorkOrderLog();
            workOrderLog.setWorkNo(workOrderNo);
            workOrderLog.setType(workOrder.getType());
            workOrderLog.setDName("admin");
            workOrderLog.setCameraLocation(workOrder.getCameraLocation());
            iWorkOrderLogService.save(workOrderLog);

        });
        return null;
    }

}
