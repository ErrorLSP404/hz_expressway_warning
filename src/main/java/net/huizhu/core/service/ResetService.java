package net.huizhu.core.service;

/**
 * <p>
 * 清除绑定关系 服务类
 * </p>
 *
 * @author huizhu
 */
public interface ResetService {

    /**
     * 删除高速路
     */
    public Boolean resetHighWay(Long higeWayId) throws Exception;

    /**
     * 删除路段
     */
    public Boolean resetSection(Long sectionId) throws Exception;

    /**
     * 删除摄像头
     */
    public Boolean resetCamera(Long cameraId) throws Exception;
}
