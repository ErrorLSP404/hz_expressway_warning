package net.huizhu.controller.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.huizhu.core.entity.AlarmScreenshot;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CameraAndMessageVo {

    /**
     * 安装位置
     */
    private String cameraLocation;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 图片对象
     */
    private List<AlarmScreenshot> alarmScreenshotList;
}
