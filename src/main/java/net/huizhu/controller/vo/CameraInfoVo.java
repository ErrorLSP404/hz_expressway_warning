package net.huizhu.controller.vo;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CameraInfoVo {

    private Long id;

    /**
     * 硬件序列号
     */
    private String cameraSerialNumber;

    /**
     * 名称
     */
    private String cameraName;

    /**
     * 品牌
     */
    private String cameraBrand;

    /**
     * 厂家
     */
    private String cameraManufacturer;

    /**
     * 制作日期
     */
    private LocalDateTime cameraManufactureTime;

    private String cameraManufactureTimeView;
    /**
     * 安装日期
     */
    private LocalDateTime cameraInstallTime;

    private String cameraInstallTimeView;

    /**
     * 维护日期
     */
    private LocalDateTime cameraMaintenanceTime;

    private String cameraMaintenanceTimeView;

    /**
     * 类型0:枪机1:云台
     */
    private Integer cameraType;

    /**
     * 状态(0:报废1:正常2:停用3:维修)
     */
    private Integer cameraState;

    /**
     * 通道号
     */
    private String cameraChannelid;

    /**
     * 安装位置
     */
    private String cameraLocation;


    /**
     * 流地址
     */
    private String cameraStream;

    /**
     * 摄像头IP
     */
    private String cameraIp;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 用户名
     */
    private String cameraUsername;

    /**
     * 密码
     */
    private String cameraPassword;


    public void setCameraManufactureTime(LocalDateTime cameraManufactureTime) {
        this.cameraManufactureTime = cameraManufactureTime;
        this.cameraManufactureTimeView = LocalDateTimeUtil.format(cameraManufactureTime, DatePattern.NORM_DATETIME_PATTERN);
    }

    public void setCameraInstallTime(LocalDateTime cameraInstallTime) {
        this.cameraInstallTime = cameraInstallTime;
        this.cameraInstallTimeView = LocalDateTimeUtil.format(cameraInstallTime, DatePattern.NORM_DATETIME_PATTERN);
    }

    public void setCameraMaintenanceTime(LocalDateTime cameraMaintenanceTime) {
        this.cameraMaintenanceTime = cameraMaintenanceTime;
        this.cameraMaintenanceTimeView = LocalDateTimeUtil.format(cameraMaintenanceTime, DatePattern.NORM_DATETIME_PATTERN);
    }


}
