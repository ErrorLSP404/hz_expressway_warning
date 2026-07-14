package net.huizhu.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import net.huizhu.common.api.CommonResult;
import net.huizhu.controller.dto.DeptResponse;
import net.huizhu.controller.vo.DeptVo;
import net.huizhu.controller.vo.OfficeVo;
import net.huizhu.core.entity.Dept;
import net.huizhu.core.entity.Office;
import net.huizhu.core.service.IDeptService;
import net.huizhu.core.service.IOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 部门表 前端控制器
 * </p>
 *
 * @author huizhu
 * @since 2022-04-28
 */
@RestController
@RequestMapping("/dept")
public class DeptController {

    @Autowired
    private IDeptService iDeptService;
    @Autowired
    private IOfficeService iOfficeService;


    /**
     * 获取部门列表
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/getAllDept")
    public CommonResult<DeptResponse> getAllDept(){

        List<DeptVo> deptVoList = new ArrayList<>();

        List<Dept> list = iDeptService.list(null);
        DeptResponse deptResponse = new DeptResponse();
        if (CollUtil.isNotEmpty(list)) {
            list.forEach(dept -> {
                DeptVo deptVo = new DeptVo();
                BeanUtil.copyProperties(dept,deptVo);
                deptVoList.add(deptVo);
            });
        } else {
            deptResponse.setDeptVoList(deptVoList);
            return CommonResult.success(deptResponse);
        }
        deptResponse.setDeptVoList(deptVoList);
        return CommonResult.success(deptResponse);
    }

    /**
     * 获取科室列表
     */
    @PreAuthorize("hasAnyRole('ADMIN','DEPT')")
    @PostMapping(value = "/getAllOffice")
    public CommonResult<DeptResponse> getAllOffice(){

        List<OfficeVo> officeVoList = new ArrayList<>();

        List<Office> officeList = iOfficeService.list();
        DeptResponse deptResponse = new DeptResponse();
        if (CollUtil.isNotEmpty(officeList)) {
            officeList.forEach(office -> {
                OfficeVo officeVo = new OfficeVo();
                BeanUtil.copyProperties(office,officeVo);
                officeVoList.add(officeVo);
            });
        } else {
            deptResponse.setOfficeVoList(officeVoList);
            return CommonResult.success(deptResponse);
        }
        deptResponse.setOfficeVoList(officeVoList);
        return CommonResult.success(deptResponse);
    }

}
