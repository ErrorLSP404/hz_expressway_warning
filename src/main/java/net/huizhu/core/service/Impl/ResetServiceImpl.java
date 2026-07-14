package net.huizhu.core.service.Impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.huizhu.core.entity.HighwaySection;
import net.huizhu.core.entity.RlSectionCamera;
import net.huizhu.core.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  重置关系 服务类
 * </p>
 *
 * @author huizhu
 */
@Service
public class ResetServiceImpl implements ResetService {

    @Autowired
    private IHighwayService highwayService;
    @Autowired
    private IHighwaySectionService highwaySectionService;
    @Autowired
    private IRlSectionCameraService sectionCameraService;
    @Autowired
    private ICameraInfoService cameraInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean resetHighWay(Long higeWayId) throws Exception{
        //删除摄像头绑定
        QueryWrapper<RlSectionCamera> rlSectionCameraQueryWrapper = new QueryWrapper<>();
        rlSectionCameraQueryWrapper.lambda().eq(RlSectionCamera::getHighwayId,higeWayId);
        sectionCameraService.remove(rlSectionCameraQueryWrapper);
        //删除路段绑定
        QueryWrapper<HighwaySection> highwaySectionQueryWrapper = new QueryWrapper<>();
        highwaySectionQueryWrapper.lambda().eq(HighwaySection::getHighwayId,higeWayId);
        highwaySectionService.remove(highwaySectionQueryWrapper);
        //删除高速路
        highwayService.removeById(higeWayId);
        return true;
    }

    @Override
    public Boolean resetSection(Long sectionId) throws Exception{
        //删除摄像头绑定
        QueryWrapper<RlSectionCamera> rlSectionCameraQueryWrapper = new QueryWrapper<>();
        rlSectionCameraQueryWrapper.lambda().eq(RlSectionCamera::getSectionId,sectionId);
        sectionCameraService.remove(rlSectionCameraQueryWrapper);
        //删除路段
        highwaySectionService.removeById(sectionId);
        return true;
    }

    @Override
    public Boolean resetCamera(Long cameraId) throws Exception{
        //删除摄像头绑定
        QueryWrapper<RlSectionCamera> rlSectionCameraQueryWrapper = new QueryWrapper<>();
        rlSectionCameraQueryWrapper.lambda().eq(RlSectionCamera::getCameraId,cameraId);
        sectionCameraService.remove(rlSectionCameraQueryWrapper);
        //删除摄像头
        cameraInfoService.removeById(cameraId);
        return true;
    }
}
