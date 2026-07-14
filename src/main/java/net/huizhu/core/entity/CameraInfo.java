package net.huizhu.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
*
* @author lsp
* @since 2021-11-30
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CameraInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * 创建时间
    */
    private LocalDateTime gmtCreate;

    /**
    * 修改时间
    */
    private LocalDateTime gmtModified;

    /**
    * 序列号
    */
    private String cameraSerialNumber;

    /**
    * 通道号
    */
    @TableField("camera_channelId")
    private String cameraChannelid;

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
    * 生产日期
    */
    private LocalDateTime cameraManufactureTime;

    /**
    * 安装日期
    */
    private LocalDateTime cameraInstallTime;

    /**
    * 维护日期
    */
    private LocalDateTime cameraMaintenanceTime;

    /**
    * 类型0:枪机1:云台
    */
    private Integer cameraType;

    /**
    * 状态(0:报废1:正常2:停用3:维修)
    */
    private Integer cameraState;

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
     * 坐标
     */
    private String cameraMapCoordinate;

    /**
     * 用户名
     */
    private String cameraUsername;

    /**
     * 密码
     */
    private String cameraPassword;


}
