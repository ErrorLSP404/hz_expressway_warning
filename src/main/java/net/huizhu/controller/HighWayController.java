package net.huizhu.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import net.huizhu.common.api.CommonResult;
import net.huizhu.controller.dto.HighwayRequest;
import net.huizhu.controller.dto.HighwayResponse;
import net.huizhu.controller.dto.SectionResquest;
import net.huizhu.controller.vo.HighWayVo;
import net.huizhu.controller.vo.HighwaySectionVo;
import net.huizhu.core.entity.Highway;
import net.huizhu.core.entity.HighwaySection;
import net.huizhu.core.entity.RlSectionCamera;
import net.huizhu.core.service.IHighwaySectionService;
import net.huizhu.core.service.IHighwayService;
import net.huizhu.core.service.IRlSectionCameraService;
import net.huizhu.core.service.ResetService;
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
@RequestMapping("/highway")
public class HighWayController {

    @Autowired
    private IHighwayService highwayService;
    @Autowired
    private IHighwaySectionService highwaySectionService;
    @Autowired
    private ResetService resetService;
    @Autowired
    private IRlSectionCameraService sectionCameraService;


    /**
     * 添加高速路
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/add")
    public CommonResult add(@RequestBody HighwayRequest highwayRequest){
        String highwayName = highwayRequest.getHighwayName();
        if(StrUtil.isBlank(highwayName)){
            return CommonResult.failed("高速公路名称不能为空");
        }
        String highwayLocation = highwayRequest.getHighwayLocation();
        String highwayMapCoordinate = highwayRequest.getHighwayMapCoordinate();
        String highwayIntroduction = highwayRequest.getHighwayIntroduction();
        Integer highwaySectionNum = highwayRequest.getHighwaySectionNum();
        Highway highway = new Highway();
        highway.setHighwayName(highwayName);
        highway.setHighwayLocation(highwayLocation);
        highway.setHighwayMapCoordinate(highwayMapCoordinate);
        highway.setHighwayIntroduction(highwayIntroduction);
        highway.setHighwaySectionNum(highwaySectionNum);
        boolean save = highwayService.save(highway);
        if(save){
            return CommonResult.success();
        }
        return CommonResult.failed("添加失败");
    }

    /**
     * 编辑高速路
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/edit")
    public CommonResult edit(@RequestBody HighwayRequest highwayRequest){
        Long id = highwayRequest.getId();
        if(id == null){
            return CommonResult.failed("未找到高速路ID");
        }
        Highway highway = highwayService.getById(id);
        if(ObjectUtil.isNull(highway)){
            return CommonResult.failed("高速路未找到");
        }
        String highwayName = highwayRequest.getHighwayName();
        if(StrUtil.isNotBlank(highwayName)){
            highway.setHighwayName(highwayName);
        }
        String highwayLocation = highwayRequest.getHighwayLocation();
        if(StrUtil.isNotBlank(highwayLocation)){
            highway.setHighwayLocation(highwayLocation);
        }
        String highwayMapCoordinate = highwayRequest.getHighwayMapCoordinate();
        if(StrUtil.isNotBlank(highwayMapCoordinate)){
            highway.setHighwayMapCoordinate(highwayMapCoordinate);
        }
        String highwayIntroduction = highwayRequest.getHighwayIntroduction();
        if(StrUtil.isNotBlank(highwayIntroduction)){
            highway.setHighwayIntroduction(highwayIntroduction);
        }
        Integer highwaySectionNum = highwayRequest.getHighwaySectionNum();
        if(highwaySectionNum != null){
            highway.setHighwaySectionNum(highwaySectionNum);
        }
        boolean update = highwayService.updateById(highway);
        if(update){
            return CommonResult.success();
        }
        return CommonResult.failed("更新失败");
    }

    /**
     * 高速路列表
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/list")
    public CommonResult<HighwayResponse> list(@RequestBody HighwayRequest highwayRequest){
        Integer pageNo = highwayRequest.getPageNo();
        Integer pageSize = highwayRequest.getPageSize();
        Page<Highway> page = highwayService.page(new Page<>(pageNo, pageSize));
        long total = page.getTotal();
        long pages = page.getPages();
        List<Highway> records = page.getRecords();
        List<HighWayVo> highWayVoList = new ArrayList<>();
        if(CollUtil.isNotEmpty(records)){
            for (Highway highway:records){
                HighWayVo highWayVo = new HighWayVo();
                BeanUtil.copyProperties(highway,highWayVo);
                highWayVoList.add(highWayVo);
            }
        }
        HighwayResponse highwayResponse = new HighwayResponse();
        highwayResponse.setTotalCount(total);
        highwayResponse.setTotalPages(pages);
        highwayResponse.setHighWayVoList(highWayVoList);
        return CommonResult.success(highwayResponse);
    }

    /**
     * 高速路详情
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/info")
    public CommonResult<HighwayResponse> info(@RequestBody HighwayRequest highwayRequest){
        Long id = highwayRequest.getId();
        if(id == null){
            return CommonResult.failed("公路ID为空");
        }
        Highway highway = highwayService.getById(id);
        HighWayVo highWayVo = new HighWayVo();
        if(ObjectUtil.isNotNull(highway)){
            BeanUtil.copyProperties(highway,highWayVo);
        }
        HighwayResponse highwayResponse = new HighwayResponse();
        highwayResponse.setHighWayVo(highWayVo);
        return CommonResult.success(highwayResponse);
    }

    /**
     * 添加高速路段
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/addSection")
    public CommonResult addSection(@RequestBody SectionResquest sectionResquest){
        String sectionName = sectionResquest.getSectionName();
        if(StrUtil.isBlank(sectionName)){
            return CommonResult.failed("路段名称为空");
        }
        Long highwayId = sectionResquest.getHighwayId();
        if(highwayId == null){
            return CommonResult.failed("公路ID为空");
        }
        Long sectionKm = sectionResquest.getSectionKm();
        Integer sectionNum = sectionResquest.getSectionNum();
        String sectionLocation = sectionResquest.getSectionLocation();
        HighwaySection highwaySection = new HighwaySection();
        highwaySection.setHighwayId(highwayId);
        highwaySection.setSectionName(sectionName);
        highwaySection.setSectionNum(sectionNum);
        highwaySection.setSectionLocation(sectionLocation);
        highwaySection.setSectionKm(sectionKm);
        boolean save = highwaySectionService.save(highwaySection);
        if(save){
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 编辑高速路段
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/editSection")
    public CommonResult editSection(@RequestBody SectionResquest sectionResquest){
        Long id = sectionResquest.getId();
        if(id == null){
            return CommonResult.failed("路段ID为空");
        }
        HighwaySection highwaySection = highwaySectionService.getById(id);
        if(ObjectUtil.isNull(highwaySection)){
            return CommonResult.failed("路段未找到");
        }
        String sectionName = sectionResquest.getSectionName();
        if(StrUtil.isNotBlank(sectionName)){
            highwaySection.setSectionName(sectionName);
        }
        Long sectionKm = sectionResquest.getSectionKm();
        if(sectionKm != null){
            highwaySection.setSectionKm(sectionKm);
        }
        Integer sectionNum = sectionResquest.getSectionNum();
        if(sectionNum != null){
            highwaySection.setSectionNum(sectionNum);
        }
        String sectionLocation = sectionResquest.getSectionLocation();
        if(StrUtil.isNotBlank(sectionLocation)){
            highwaySection.setSectionLocation(sectionLocation);
        }
        boolean update = highwaySectionService.updateById(highwaySection);
        QueryWrapper<RlSectionCamera> rlSectionCameraQueryWrapper = new QueryWrapper<>();
        rlSectionCameraQueryWrapper.lambda().eq(RlSectionCamera::getSectionId,highwaySection.getId());
        int count = sectionCameraService.count(rlSectionCameraQueryWrapper);
        if(count > 0){
            RlSectionCamera rlSectionCamera = new RlSectionCamera();
            rlSectionCamera.setSectionName(highwaySection.getSectionName());
            boolean updatesection = sectionCameraService.update(rlSectionCamera, rlSectionCameraQueryWrapper);
            if(update && updatesection){
                return CommonResult.success();
            }
        }else {
            if(update){
                return CommonResult.success();
            }
        }
        return CommonResult.failed("更新失败");
    }

    /**
     * 高速路段列表
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/listSection")
    public CommonResult<HighwayResponse> listSection(@RequestBody SectionResquest sectionResquest){
        Long highwayId = sectionResquest.getHighwayId();
        if(highwayId == null){
            return CommonResult.failed("高速公路ID为空");
        }
        Integer pageNo = sectionResquest.getPageNo();
        Integer pageSize = sectionResquest.getPageSize();
        QueryWrapper<HighwaySection> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(HighwaySection::getHighwayId,highwayId);
        Page<HighwaySection> page = highwaySectionService.page(new Page<>(pageNo, pageSize), queryWrapper);
        long total = page.getTotal();
        long pages = page.getPages();
        HighwayResponse highwayResponse = new HighwayResponse();
        highwayResponse.setTotalCount(total);
        highwayResponse.setTotalPages(pages);
        List<HighwaySection> records = page.getRecords();
        List<HighwaySectionVo> sectionVoList = new ArrayList<>();
        if(CollUtil.isNotEmpty(records)){
            for (HighwaySection highwaySection:records) {
                HighwaySectionVo highwaySectionVo = new HighwaySectionVo();
                BeanUtil.copyProperties(highwaySection,highwaySectionVo);
                sectionVoList.add(highwaySectionVo);
            }
        }
        highwayResponse.setHighwaySectionVoList(sectionVoList);
        return CommonResult.success(highwayResponse);
    }

    /**
     * 高速路段详情
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/infoSection")
    public CommonResult<HighwayResponse> infoSection(@RequestBody SectionResquest sectionResquest){
        Long id = sectionResquest.getId();
        if(id == null){
            return CommonResult.failed("路段ID为空");
        }
        HighwayResponse highwayResponse = new HighwayResponse();
        HighwaySection highwaySection = highwaySectionService.getById(id);
        HighwaySectionVo highwaySectionVo = new HighwaySectionVo();
        if(ObjectUtil.isNotNull(highwaySection)){
            BeanUtil.copyProperties(highwaySection,highwaySectionVo);
        }
        highwayResponse.setHighwaySectionVo(highwaySectionVo);
        return CommonResult.success(highwayResponse);
    }

    /**
     * 删除高速路
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/delete")
    public CommonResult delete(@RequestBody HighwayRequest highwayRequest){
        Long id = highwayRequest.getId();
        if(id == null){
            return CommonResult.failed("高速公路ID为空");
        }
        try {
            Boolean resetHighWay = resetService.resetHighWay(id);
            if(resetHighWay){
                return CommonResult.success();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return CommonResult.failed();
    }

    /**
     * 删除高速路段
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/deleteSection")
    public CommonResult deleteSection(@RequestBody SectionResquest sectionResquest){
        Long id = sectionResquest.getId();
        if(id == null){
            return CommonResult.failed("路段ID为空");
        }
        try {
            Boolean resetSection = resetService.resetSection(id);
            if(resetSection){
                return CommonResult.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonResult.failed();
    }

    /**
     * 路线&桩号下拉框
     */
    /**
     * 删除高速路段
     */
    @PreAuthorize("hasAnyRole('ADMIN','DEPT','OFFICE')")
    @PostMapping(value = "/getAllHighWay")
    public CommonResult<HighwayResponse> getAllHighWay(){
        List<Highway> highways = highwayService.list();
        List<HighWayVo> highWayVoList = new ArrayList<>();
        HighwayResponse highwayResponse = new HighwayResponse();
        if (CollUtil.isNotEmpty(highways)) {
            highways.forEach(highway -> {
                HighWayVo highWayVo = new HighWayVo();
                BeanUtil.copyProperties(highway,highWayVo);
                highWayVoList.add(highWayVo);
            });
        }
        highwayResponse.setHighWayVoList(highWayVoList);
        return CommonResult.success(highwayResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN','DEPT','OFFICE')")
    @PostMapping(value = "/getAllSection")
    public CommonResult<HighwayResponse> getAllSection(@RequestBody SectionResquest sectionResquest){
        Long highwayId = sectionResquest.getHighwayId();
        if (highwayId == null) {
            return CommonResult.failed("路线Id不能为空");
        }
        List<HighwaySectionVo> highwaySectionVoList = new ArrayList<>();
        QueryWrapper<HighwaySection> highwaySectionQueryWrapper = new QueryWrapper<>();
        highwaySectionQueryWrapper.lambda().eq(HighwaySection::getHighwayId,highwayId);
        List<HighwaySection> list = highwaySectionService.list(highwaySectionQueryWrapper);
        if (CollUtil.isNotEmpty(list)) {
            list.forEach(highwaySection -> {
                HighwaySectionVo highwaySectionVo = new HighwaySectionVo();
                BeanUtil.copyProperties(highwaySection,highwaySectionVo);
                highwaySectionVoList.add(highwaySectionVo);
            });
        }
        HighwayResponse highwayResponse = new HighwayResponse();
        highwayResponse.setHighwaySectionVoList(highwaySectionVoList);
        return CommonResult.success(highwayResponse);
    }
}
