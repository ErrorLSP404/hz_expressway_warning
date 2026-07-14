package net.huizhu.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.huizhu.common.api.CommonResult;
import net.huizhu.common.enums.CameraBrandEnum;
import net.huizhu.controller.dto.CameraInfoRequest;
import net.huizhu.controller.dto.CameraInfoResponse;
import net.huizhu.controller.vo.CameraBrandVo;
import net.huizhu.controller.vo.CameraInfoVo;
import net.huizhu.core.entity.CameraInfo;
import net.huizhu.core.entity.RlSectionCamera;
import net.huizhu.core.service.ICameraInfoService;
import net.huizhu.core.service.IRlSectionCameraService;
import net.huizhu.core.service.InitDataService;
import net.huizhu.core.service.ResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/camera")
public class CameraController {

    @Autowired
    private ICameraInfoService cameraInfoService;
    @Autowired
    private ResetService resetService;
    @Autowired
    private IRlSectionCameraService sectionCameraService;
    @Autowired
    private InitDataService initDataService;

    /**
     * 获取摄像头品牌
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/getAllBrand")
    public CommonResult<CameraInfoResponse> getAllBrand(){

        CameraInfoResponse cameraInfoResponse = new CameraInfoResponse();
        List<CameraBrandVo> cameraBrandVoList = new ArrayList<>();
        CameraBrandEnum[] values = CameraBrandEnum.values();
        for (CameraBrandEnum cameraBrandEnum: values) {
            CameraBrandVo cameraBrandVo = new CameraBrandVo();
            String code = cameraBrandEnum.getCode();
            String desc = cameraBrandEnum.getDesc();
            cameraBrandVo.setCode(code);
            cameraBrandVo.setDesc(desc);
            cameraBrandVoList.add(cameraBrandVo);
        }
        cameraInfoResponse.setCameraBrandVoList(cameraBrandVoList);
        return CommonResult.success(cameraInfoResponse);
    }


    /**
     * 添加摄像头
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/add")
    public CommonResult add(@RequestBody CameraInfoRequest cameraInfoRequest){
        String cameraSerialNumber = cameraInfoRequest.getCameraSerialNumber();
//        if(StrUtil.isBlank(cameraSerialNumber)){
//            return CommonResult.failed("序列号参数缺失");
//        }
        String cameraName = cameraInfoRequest.getCameraName();
        if(StrUtil.isBlank(cameraName)){
            return CommonResult.failed("名称参数缺失");
        }

        String cameraChannelid = cameraInfoRequest.getCameraChannelid();
        if (StrUtil.isBlank(cameraChannelid)){
            return CommonResult.failed("channelid参数缺失");
        }
        String cameraIp = cameraInfoRequest.getCameraIp();
        if(StrUtil.isBlank(cameraIp)){
            return CommonResult.failed("cameraIp参数缺失");
        }
        String cameraUsername = cameraInfoRequest.getCameraUsername();
        if(StrUtil.isBlank(cameraUsername)){
            return CommonResult.failed("cameraUsername参数缺失");
        }
        String cameraPassword = cameraInfoRequest.getCameraPassword();
        if(StrUtil.isBlank(cameraPassword)){
            return CommonResult.failed("cameraPassword参数缺失");
        }
        String cameraBrand = cameraInfoRequest.getCameraBrand();
        String cameraManufacturer = cameraInfoRequest.getCameraManufacturer();
        String cameraManufactureTime = cameraInfoRequest.getCameraManufactureTime();
        LocalDateTime ManufactureTime = LocalDateTimeUtil.of(DateUtil.parse(cameraManufactureTime));
        String cameraInstallTime = cameraInfoRequest.getCameraInstallTime();
        LocalDateTime InstallTime = LocalDateTimeUtil.of(DateUtil.parse(cameraInstallTime));
        String cameraMaintenanceTime = cameraInfoRequest.getCameraMaintenanceTime();
        LocalDateTime MaintenanceTime = LocalDateTimeUtil.of(DateUtil.parse(cameraMaintenanceTime));
        Integer cameraType = cameraInfoRequest.getCameraType();
        Integer cameraState = cameraInfoRequest.getCameraState();
        String cameraStream = cameraInfoRequest.getCameraStream();
        String cameraLocation = cameraInfoRequest.getCameraLocation();
        //经度
        String longitude = cameraInfoRequest.getLongitude();
        //纬度
        String latitude = cameraInfoRequest.getLatitude();
        CameraInfo cameraInfo = new CameraInfo();
        cameraInfo.setCameraSerialNumber(cameraSerialNumber);
        cameraInfo.setCameraName(cameraName);
        cameraInfo.setCameraChannelid(cameraChannelid);
        cameraInfo.setCameraBrand(cameraBrand);
        cameraInfo.setCameraManufacturer(cameraManufacturer);
        cameraInfo.setCameraManufactureTime(ManufactureTime);
        cameraInfo.setCameraInstallTime(InstallTime);
        cameraInfo.setCameraMaintenanceTime(MaintenanceTime);
        cameraInfo.setCameraType(cameraType);
        cameraInfo.setCameraState(cameraState);
        cameraInfo.setCameraStream(cameraStream);
        cameraInfo.setCameraLocation(cameraLocation);
        cameraInfo.setCameraIp(cameraIp);
        cameraInfo.setCameraUsername(cameraUsername);
        cameraInfo.setCameraPassword(cameraPassword);
        cameraInfo.setCameraMapCoordinate(longitude+"#"+latitude);
        boolean save = cameraInfoService.save(cameraInfo);
        if(save){
            return CommonResult.success();
        }
        return CommonResult.failed();
    }


    /**
     * 编辑摄像头
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/edit")
    public CommonResult edit(@RequestBody CameraInfoRequest cameraInfoRequest){
        Long id = cameraInfoRequest.getId();
        if(id == null){
            return CommonResult.failed("id缺失");
        }
        CameraInfo cameraInfo = cameraInfoService.getById(id);
        if(cameraInfo == null){
            return CommonResult.failed("未找到摄像头");
        }
        String cameraSerialNumber = cameraInfoRequest.getCameraSerialNumber();
        if(StrUtil.isNotBlank(cameraSerialNumber)){
            cameraInfo.setCameraSerialNumber(cameraSerialNumber);
        }
        String cameraName = cameraInfoRequest.getCameraName();
        if(StrUtil.isNotBlank(cameraName)){
            cameraInfo.setCameraName(cameraName);
        }

        String cameraChannelid = cameraInfoRequest.getCameraChannelid();
        if (StrUtil.isNotBlank(cameraChannelid)){
            cameraInfo.setCameraChannelid(cameraChannelid);
        }
        String cameraBrand = cameraInfoRequest.getCameraBrand();
        if(StrUtil.isNotBlank(cameraBrand)){
            cameraInfo.setCameraBrand(cameraBrand);
        }
        String cameraManufacturer = cameraInfoRequest.getCameraManufacturer();
        if(StrUtil.isNotBlank(cameraManufacturer)){
            cameraInfo.setCameraManufacturer(cameraManufacturer);
        }
        String cameraManufactureTime = cameraInfoRequest.getCameraManufactureTime();
        if(StrUtil.isNotBlank(cameraManufactureTime)){
            LocalDateTime ManufactureTime = LocalDateTimeUtil.of(DateUtil.parse(cameraManufactureTime));
            cameraInfo.setCameraManufactureTime(ManufactureTime);
        }
        String cameraInstallTime = cameraInfoRequest.getCameraInstallTime();
        if(StrUtil.isNotBlank(cameraInstallTime)){
            LocalDateTime InstallTime = LocalDateTimeUtil.of(DateUtil.parse(cameraInstallTime));
            cameraInfo.setCameraInstallTime(InstallTime);
        }
        String cameraMaintenanceTime = cameraInfoRequest.getCameraMaintenanceTime();
        if(StrUtil.isNotBlank(cameraMaintenanceTime)){
            LocalDateTime MaintenanceTime = LocalDateTimeUtil.of(DateUtil.parse(cameraMaintenanceTime));
            cameraInfo.setCameraMaintenanceTime(MaintenanceTime);
        }
        Integer cameraType = cameraInfoRequest.getCameraType();
        if(cameraType != null){
            cameraInfo.setCameraType(cameraType);
        }
        Integer cameraState = cameraInfoRequest.getCameraState();
        if(cameraState != null){
            cameraInfo.setCameraState(cameraState);
        }
        String cameraLocation = cameraInfoRequest.getCameraLocation();
        if(StrUtil.isNotBlank(cameraLocation)){
            cameraInfo.setCameraLocation(cameraLocation);
        }
        String cameraStream = cameraInfoRequest.getCameraStream();
        if(StrUtil.isNotBlank(cameraStream)){
            cameraInfo.setCameraStream(cameraStream);
        }
        String cameraIp = cameraInfoRequest.getCameraIp();
        if(StrUtil.isNotBlank(cameraIp)){
            cameraInfo.setCameraIp(cameraIp);
        }
        String cameraUsername = cameraInfoRequest.getCameraUsername();
        if(StrUtil.isNotBlank(cameraUsername)){
            cameraInfo.setCameraUsername(cameraUsername);
        }
        String cameraPassword = cameraInfoRequest.getCameraPassword();
        if(StrUtil.isNotBlank(cameraPassword)){
           cameraInfo.setCameraPassword(cameraPassword);
        }
        //经度
        String longitude = cameraInfoRequest.getLongitude();
        //纬度
        String latitude = cameraInfoRequest.getLatitude();
        if(StrUtil.isNotBlank(longitude) && StrUtil.isNotBlank(latitude)){
            cameraInfo.setCameraMapCoordinate(longitude+"#"+latitude);
        }
        boolean result = cameraInfoService.updateById(cameraInfo);
        QueryWrapper<RlSectionCamera> rlSectionCameraQueryWrapper = new QueryWrapper<>();
        rlSectionCameraQueryWrapper.lambda().eq(RlSectionCamera::getCameraId,cameraInfo.getId());
        int count = sectionCameraService.count(rlSectionCameraQueryWrapper);
        if(count > 0){
            RlSectionCamera rlSectionCamera = new RlSectionCamera();
            rlSectionCamera.setCameraName(cameraInfo.getCameraName());
            boolean update = sectionCameraService.update(rlSectionCamera, rlSectionCameraQueryWrapper);
            if(result && update){
                initDataService.initCamera();
                return CommonResult.success();
            }
        }else {
            if(result){
                return CommonResult.success();
            }
        }
        return CommonResult.failed();
    }


    /**
     * 获取摄像头信息
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/camerainfo")
    public CommonResult<CameraInfoResponse> camerainfo(@RequestBody CameraInfoRequest cameraInfoRequest){
        Long id = cameraInfoRequest.getId();
        if(id == null){
            return CommonResult.failed("id缺失");
        }
        CameraInfo cameraInfo = cameraInfoService.getById(id);
        if(!ObjectUtil.isNull(cameraInfo)){
            CameraInfoResponse cameraInfoResponse = new CameraInfoResponse();
            CameraInfoVo cameraInfoVo = new CameraInfoVo();
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
            cameraInfoResponse.setCameraInfoVo(cameraInfoVo);
            return CommonResult.success(cameraInfoResponse);
        }
        return CommonResult.failed();
    }


    /**
     * 获取摄像头列表
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/cameralist")
    public CommonResult<CameraInfoResponse> cameralist(@RequestBody CameraInfoRequest cameraInfoRequest){
        Integer pageNo = cameraInfoRequest.getPageNo();
        Integer pageSize = cameraInfoRequest.getPageSize();
        QueryWrapper<CameraInfo> queryWrapper = new QueryWrapper<>();
        Page<CameraInfo> page = cameraInfoService.page(new Page<>(pageNo, pageSize),queryWrapper);
        CameraInfoResponse cameraInfoResponse = new CameraInfoResponse();
        long total = page.getTotal();
        long pages = page.getPages();
        cameraInfoResponse.setTotalCount(total);
        cameraInfoResponse.setTotalPages(pages);
        List<CameraInfo> records = page.getRecords();
        List<CameraInfoVo> cameraInfoVoList = new ArrayList<>();
        if (CollUtil.isNotEmpty(records)){
            for (CameraInfo cameraInfo:records) {
                CameraInfoVo cameraInfoVo = new CameraInfoVo();
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
        cameraInfoResponse.setCameraInfoVoList(cameraInfoVoList);
        return CommonResult.success(cameraInfoResponse);
    }

    /**
     * 获取所有摄像头
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/getAllcamera")
    public CommonResult<CameraInfoResponse> getAllcamera(){
        QueryWrapper<CameraInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CameraInfo::getCameraState,1);
        List<CameraInfo> list = cameraInfoService.list(queryWrapper);
        CameraInfoResponse cameraInfoResponse = new CameraInfoResponse();
        List<CameraInfoVo> cameraInfoVoList = new ArrayList<>();
        if(CollUtil.isNotEmpty(list)){
            for (CameraInfo cameraInfo :list) {
                CameraInfoVo cameraInfoVo = new CameraInfoVo();
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
        cameraInfoResponse.setCameraInfoVoList(cameraInfoVoList);
        return CommonResult.success(cameraInfoResponse);
    }

    /**
     * 删除摄像头
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/deletecamera")
    public CommonResult deletecamera(@RequestBody CameraInfoRequest cameraInfoRequest){
        Long id = cameraInfoRequest.getId();
        if(id == null){
            return CommonResult.failed("摄像头ID为空");
        }
        try {
            Boolean resetCamera = resetService.resetCamera(id);
            if(resetCamera){
                initDataService.initCamera();
                return CommonResult.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonResult.failed();
    }

}
