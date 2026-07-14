package net.huizhu.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.huizhu.common.api.CommonResult;
import net.huizhu.controller.dto.SectionCameraRequest;
import net.huizhu.controller.dto.SectionCameraResponse;
import net.huizhu.controller.vo.RlSectionCameraVo;
import net.huizhu.core.entity.RlSectionCamera;
import net.huizhu.core.service.ICameraInfoService;
import net.huizhu.core.service.IHighwaySectionService;
import net.huizhu.core.service.IRlSectionCameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rlsectioncamera")
public class SectionCameraController {


    @Autowired
    private ICameraInfoService cameraInfoService;
    @Autowired
    private IHighwaySectionService highwaySectionService;
    @Autowired
    private IRlSectionCameraService sectionCameraService;


    /**
     * 添加绑定
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/add")
    public CommonResult add(@RequestBody SectionCameraRequest sectionCameraRequest){
        Long highwayId = sectionCameraRequest.getHighwayId();
        if(highwayId == null){
            return CommonResult.failed("高速公路ID");
        }
        Long sectionId = sectionCameraRequest.getSectionId();
        if(sectionId == null){
            return CommonResult.failed("路段ID为空");
        }
        String sectionName = sectionCameraRequest.getSectionName();
        if(StrUtil.isBlank(sectionName)){
            return CommonResult.failed("路段名称为空");
        }
        Long cameraId = sectionCameraRequest.getCameraId();
        if(cameraId == null){
            return CommonResult.failed("摄像头ID为空");
        }
        String cameraName = sectionCameraRequest.getCameraName();
        if(StrUtil.isBlank(cameraName)){
            return CommonResult.failed("摄像头名称为空");
        }
        QueryWrapper<RlSectionCamera> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(RlSectionCamera::getCameraId,cameraId);
        int count = sectionCameraService.count(queryWrapper);
        if(count > 0){
            return CommonResult.failed("摄像头已被绑定");
        }
        RlSectionCamera rlSectionCamera = new RlSectionCamera();
        rlSectionCamera.setHighwayId(highwayId);
        rlSectionCamera.setSectionId(sectionId);
        rlSectionCamera.setSectionName(sectionName);
        rlSectionCamera.setCameraId(cameraId);
        rlSectionCamera.setCameraName(cameraName);
        boolean save = sectionCameraService.save(rlSectionCamera);
        if(save){
            return CommonResult.success();
        }
        return CommonResult.failed("添加失败");
    }

    /**
     * 获取绑定关系(依据路段)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/listBySection")
    public  CommonResult listBySection(@RequestBody SectionCameraRequest sectionCameraRequest){
        Long highwayId = sectionCameraRequest.getHighwayId();
        if(highwayId == null){
            return CommonResult.failed("公路ID为空");
        }
        Long sectionId = sectionCameraRequest.getSectionId();
        if(sectionId == null){
            return CommonResult.failed("路段ID为空");
        }
        QueryWrapper<RlSectionCamera> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(RlSectionCamera::getSectionId,sectionId).eq(RlSectionCamera::getHighwayId,highwayId);
        List<RlSectionCamera> list = sectionCameraService.list(queryWrapper);
        List<RlSectionCameraVo> rlSectionCameraVoList = new ArrayList<>();
        SectionCameraResponse sectionCameraResponse = new SectionCameraResponse();
        if(CollUtil.isNotEmpty(list)){
            for (RlSectionCamera rlSectionCamera:list){
                RlSectionCameraVo rlSectionCameraVo = new RlSectionCameraVo();
                BeanUtil.copyProperties(rlSectionCamera,rlSectionCameraVo);
                rlSectionCameraVoList.add(rlSectionCameraVo);
            }
        }
        sectionCameraResponse.setSectionCameraVoList(rlSectionCameraVoList);
        return CommonResult.success(sectionCameraResponse);
    }

    /**
     * 获取绑定关系(依据摄像头)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/listByCamera")
    public  CommonResult listByCamera(@RequestBody SectionCameraRequest sectionCameraRequest){
        Long highwayId = sectionCameraRequest.getHighwayId();
        if(highwayId == null){
            return CommonResult.failed("公路ID为空");
        }
        Long cameraId = sectionCameraRequest.getCameraId();
        if(cameraId == null){
            return CommonResult.failed("摄像头ID为空");
        }
        QueryWrapper<RlSectionCamera> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(RlSectionCamera::getCameraId,cameraId).eq(RlSectionCamera::getHighwayId,highwayId);
        List<RlSectionCamera> list = sectionCameraService.list(queryWrapper);
        List<RlSectionCameraVo> rlSectionCameraVoList = new ArrayList<>();
        SectionCameraResponse sectionCameraResponse = new SectionCameraResponse();
        if(CollUtil.isNotEmpty(list)){
            for (RlSectionCamera rlSectionCamera:list){
                RlSectionCameraVo rlSectionCameraVo = new RlSectionCameraVo();
                BeanUtil.copyProperties(rlSectionCamera,rlSectionCameraVo);
                rlSectionCameraVoList.add(rlSectionCameraVo);
            }
        }
        sectionCameraResponse.setSectionCameraVoList(rlSectionCameraVoList);
        return CommonResult.success(sectionCameraResponse);
    }



    /**
     * 删除绑定
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/delete")
    public CommonResult delete(@RequestBody SectionCameraRequest sectionCameraRequest){
        Long id = sectionCameraRequest.getId();
        if(id == null){
            return CommonResult.failed("ID为空");
        }
        boolean remove = sectionCameraService.removeById(id);
        if(remove){
            return CommonResult.success();
        }
        return CommonResult.failed("删除失败");
    }



}
