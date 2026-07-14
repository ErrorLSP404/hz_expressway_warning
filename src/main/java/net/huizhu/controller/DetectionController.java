package net.huizhu.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.huizhu.common.api.CommonResult;
import net.huizhu.common.constant.RedisConstant;
import net.huizhu.common.util.NOUtil;
import net.huizhu.controller.dto.DetectionRequest;
import net.huizhu.controller.dto.HighwayResponse;
import net.huizhu.core.entity.*;
import net.huizhu.core.service.*;
import net.huizhu.rabbit.RabbitSender;
import net.huizhu.rabbit.entity.AlarmParameter;
import net.huizhu.rabbit.entity.CollisionMsg;
import net.huizhu.rabbit.entity.HighWayAssist;
import net.huizhu.rabbit.entity.SectionAssist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/highway")
public class DetectionController {

    @Autowired
    private IAlarmLogService alarmLogService;
    @Autowired
    private IAlarmScreenshotService alarmScreenshotService;
    @Autowired
    private IHighwayService highwayService;
    @Autowired
    private IHighwaySectionService highwaySectionServicel;
    @Autowired
    private RabbitSender rabbitSender;
    @Autowired
    private ICameraInfoService cameraInfoService;
    @Autowired
    private IRlSectionCameraService sectionCameraService;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private IWorkOrderService iWorkOrderService;
    @Autowired
    private IWorkOrderLogService iWorkOrderLogService;


    /**
     * 接受报警信息
     */
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping(value = "/receiveAlarm")
    public CommonResult<HighwayResponse> receiveAlarm(@RequestBody DetectionRequest detectionRequest){
        if(detectionRequest != null){
            AlarmLog alarmLog = new AlarmLog();

            String msgId = detectionRequest.getMsgId();
            if(StrUtil.isNotBlank(msgId)){
                alarmLog.setMsgId(msgId);
            }
            String timestamp = detectionRequest.getTimestamp();
            if(StrUtil.isBlank(timestamp)){
                return CommonResult.failed("系统时间错误");
            }
            Long time = Long.valueOf(timestamp);
            long now = System.currentTimeMillis();
            if(time > now){
                return CommonResult.failed("系统时间错误");
            }
            String deviceId = detectionRequest.getDeviceId();
            if(StrUtil.isBlank(deviceId)){
                return CommonResult.failed("摄像头ID为空");
            }
            //查询摄像头
//            QueryWrapper<CameraInfo> cameraInfoQueryWrapper = new QueryWrapper<>();
//            cameraInfoQueryWrapper.lambda().eq(CameraInfo::getId,deviceId);
//            CameraInfo cameraInfo = cameraInfoService.getOne(cameraInfoQueryWrapper);
//            if(ObjectUtil.isNull(cameraInfo)){
//                return CommonResult.failed("摄像头未找到");
//            }
            String json = (String) redisTemplate.opsForHash().get(RedisConstant.CAMERA, String.valueOf(deviceId));
            if(StrUtil.isBlank(json)){
                return CommonResult.failed("摄像头未找到");
            }
            CameraInfo cameraInfo = JSONUtil.toBean(json, CameraInfo.class);
            String channelId = detectionRequest.getChannelId();
            String position = detectionRequest.getPosition();
            Long cameraInfoId = cameraInfo.getId();
            alarmLog.setCameraId(cameraInfoId);
            alarmLog.setCameraName(cameraInfo.getCameraName());
            alarmLog.setCameraSerialNumber(String.valueOf(cameraInfo.getId()));
            alarmLog.setCameraLocation(cameraInfo.getCameraLocation());
            alarmLog.setCameraChannelid(cameraInfo.getCameraChannelid());
            //查询路段
            QueryWrapper<RlSectionCamera> rlSectionCameraQueryWrapper = new QueryWrapper<>();
            rlSectionCameraQueryWrapper.lambda().eq(RlSectionCamera::getCameraId,cameraInfoId);
            RlSectionCamera rlSectionCamera = sectionCameraService.getOne(rlSectionCameraQueryWrapper);
            if(ObjectUtil.isNull(rlSectionCamera)){
                return CommonResult.failed("摄像头未绑定路段");
            }
            Long sectionId = rlSectionCamera.getSectionId();
            if(sectionId == null){
                return CommonResult.failed("路段ID为空");
            }
            HighwaySection highwaySection = highwaySectionServicel.getById(sectionId);
            if(ObjectUtil.isNull(highwaySection)){
                return CommonResult.failed("路段未找到");
            }
            //查询公路
            Long highwayId = rlSectionCamera.getHighwayId();
            if(highwayId == null){
                return CommonResult.failed("公路ID为空");
            }
            Highway highway = highwayService.getById(highwayId);
            if(ObjectUtil.isNull(highway)){
                return CommonResult.failed("公路未找到");
            }
            alarmLog.setHighwayId(highwayId);
            alarmLog.setSectionId(sectionId);
            alarmLog.setSectionName(highwaySection.getSectionName());
            alarmLog.setSectionLocation(highwaySection.getSectionLocation());
            AlarmParameter alarmParameter = new AlarmParameter();
            HighWayAssist highWayAssist = new HighWayAssist();
            highWayAssist.setHighwayId(highwayId);
            highWayAssist.setHighwayName(highway.getHighwayName());
            SectionAssist sectionAssist = new SectionAssist();
            sectionAssist.setSectionid(sectionId);
            sectionAssist.setSectionName(highwaySection.getSectionName());
            sectionAssist.setSectionLocation(highwaySection.getSectionLocation());
            sectionAssist.setSectionKm(highwaySection.getSectionKm());
            highWayAssist.setSectionAssist(sectionAssist);
            alarmParameter.setHighWayAssist(highWayAssist);
            alarmLog.setAlarmParam(JSONUtil.toJsonStr(alarmParameter));
            String alarmTime = detectionRequest.getAlarmTime();
            if(StrUtil.isNotBlank(alarmTime)){
                LocalDateTime localDateTime = LocalDateTimeUtil.parse(alarmTime, DatePattern.NORM_DATETIME_PATTERN);
                alarmLog.setAlarmTime(localDateTime);
            }else {
                LocalDateTime localDateTime = LocalDateTimeUtil.now();
                alarmLog.setAlarmTime(localDateTime);
            }
            String alarmPosition = detectionRequest.getAlarmPosition();
            alarmLog.setAlarmLocation(alarmPosition);
            String alarmMsg = detectionRequest.getAlarmMsg();
            alarmLog.setContent(alarmMsg);
            Integer algorithmNum = detectionRequest.getAlgorithmNum();
            alarmLog.setAlgorithmNum(algorithmNum);
            alarmLog.setType(algorithmNum);
            String algorithmName = detectionRequest.getAlgorithmName();
            alarmLog.setAlgorithmName(algorithmName);
            //存入报警信息
            boolean save = alarmLogService.save(alarmLog);
            // 工单
            WorkOrder workOrder = new WorkOrder();
            Long workOrderNo = NOUtil.getWorkOrderNo();
            workOrder.setHighwayName(highway.getHighwayName());
            workOrder.setCameraLocation(cameraInfo.getCameraLocation());
            workOrder.setHighwayId(highwayId);
            workOrder.setSectionId(sectionId);
            workOrder.setAId(alarmLog.getId());
            workOrder.setType(algorithmNum);
            workOrder.setWorkNo(workOrderNo);
            iWorkOrderService.save(workOrder);
            // 工单记录
            WorkOrderLog workOrderLog = new WorkOrderLog();
            workOrderLog.setWorkNo(workOrderNo);
            workOrderLog.setType(algorithmNum);
            workOrderLog.setDName("admin");
            workOrderLog.setCameraLocation(cameraInfo.getCameraLocation());
            iWorkOrderLogService.save(workOrderLog);

            if(save){
                //报警图片
                String captureImage = detectionRequest.getCaptureImage();
                if(StrUtil.isNotBlank(captureImage)){
                    AlarmScreenshot alarmScreenshot = new AlarmScreenshot();
                    alarmScreenshot.setAlarmId(alarmLog.getId());
                    alarmScreenshot.setType(1);
                    alarmScreenshot.setUrl(captureImage);
                    alarmScreenshotService.save(alarmScreenshot);
                }
                String videoSrc = detectionRequest.getVideoSrc();
                if(StrUtil.isNotBlank(videoSrc)){
                //报警视频
                    AlarmScreenshot alarmScreenshot = new AlarmScreenshot();
                    alarmScreenshot.setAlarmId(alarmLog.getId());
                    alarmScreenshot.setType(2);
                    alarmScreenshot.setUrl(videoSrc);
                    alarmScreenshotService.save(alarmScreenshot);
                }
                //发送队列
                CollisionMsg collisionMsg = new CollisionMsg();
                collisionMsg.setCameraId(cameraInfo.getId());
                collisionMsg.setCameraName(cameraInfo.getCameraName());
                collisionMsg.setCameraSerialNumber(cameraInfo.getCameraSerialNumber());
                collisionMsg.setSectionId(highwaySection.getId());
                collisionMsg.setSectionName(highwaySection.getSectionName());
                collisionMsg.setSectionLocation(highwaySection.getSectionLocation());
                collisionMsg.setAlarmLocation(alarmPosition);
                collisionMsg.setType(algorithmNum);
                collisionMsg.setMessage(alarmMsg);
                collisionMsg.setTimeView(alarmTime);
                collisionMsg.setAlarmParameter(alarmParameter);
                collisionMsg.setStatus(0);
                String cameraMapCoordinate = cameraInfo.getCameraMapCoordinate();
                String longitude = StrUtil.subBefore(cameraMapCoordinate, "#", false);
                String latitude = StrUtil.subAfter(cameraMapCoordinate,"#",false);
                collisionMsg.setLongitude(longitude);
                collisionMsg.setLatitude(latitude);
                try {
                    rabbitSender.sendFanout(collisionMsg);
                    return CommonResult.success();
                } catch (Exception e) {
                    e.printStackTrace();
                    return CommonResult.failed("发送队列失败");
                }
            }
        }
        return CommonResult.failed();
    }
}
