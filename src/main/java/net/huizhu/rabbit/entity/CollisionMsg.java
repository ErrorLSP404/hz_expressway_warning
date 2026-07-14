package net.huizhu.rabbit.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CollisionMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 摄像头ID
     */
    private Long cameraId;

    /**
     * 摄像头名称
     */
    private String cameraName;

    /**
     * 序列号
     */
    private String cameraSerialNumber;

    /**
     * 路段ID
     */
    private Long sectionId;

    /**
     * 路段名称
     */
    private String sectionName;

    /**
     * 路段位置
     */
    private String sectionLocation;

    /**
     * 报警位置
     */
    private String alarmLocation;

    /**
     * 报警类型1:交通事件2:路面异物3:公路标志标线损坏4:安防设施损坏5:非法占用公路行为
     */
    private Integer type;


    /**
     * 报警参数对象
     */
    private AlarmParameter alarmParameter;


    /**
     * 消息
     */
    private String message;

    /**
     * 时间
     */
    private String timeView;
    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 状态
     */
    private Integer status;


}
