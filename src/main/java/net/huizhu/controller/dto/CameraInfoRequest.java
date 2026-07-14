package net.huizhu.controller.dto;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class CameraInfoRequest extends BaseRequest{

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
    private String cameraManufactureTime;

    /**
     * 安装日期
     */
    private String cameraInstallTime;

    /**
     * 维护日期
     */
    private String cameraMaintenanceTime;

    /**
     * 类型0:枪机1:云台
     */
    private Integer cameraType;

    /**
     * 状态(0:报废1:正常2:停用3:维修)
     */
    private Integer cameraState = 1;

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
}
