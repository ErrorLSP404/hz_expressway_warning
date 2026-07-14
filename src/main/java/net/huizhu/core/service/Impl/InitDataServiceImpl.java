package net.huizhu.core.service.Impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.huizhu.common.constant.RedisConstant;
import net.huizhu.core.entity.CameraInfo;
import net.huizhu.core.service.ICameraInfoService;
import net.huizhu.core.service.InitDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lsp
 * @since 2021-11-30
 */
@Service
public class InitDataServiceImpl implements InitDataService {

    @Autowired
    private ICameraInfoService cameraInfoService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    @Override
    public void initCamera() {
        QueryWrapper<CameraInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CameraInfo::getCameraState,1);
        List<CameraInfo> list = cameraInfoService.list(queryWrapper);
        if(CollUtil.isNotEmpty(list)){
            for (CameraInfo cameraInfo:list) {
                String cameraInfoId = String.valueOf(cameraInfo.getId());
                redisTemplate.opsForHash().put(RedisConstant.CAMERA,cameraInfoId, JSON.toJSONString(cameraInfo));
            }
        }
    }
}
