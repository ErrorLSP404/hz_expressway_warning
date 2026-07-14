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
public class AlarmLog implements Serializable {

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
    * 安装位置
    */
    private String cameraLocation;

    /**
    * 通道号
    */
    @TableField("camera_channelId")
    private String cameraChannelid;

    /**
     * 公路ID
     */
    private Long highwayId;

    /**
    * 路段ID
    */
    private Long sectionId;

    /**
    * 路段名称
    */
    private String sectionName;

    /**
    * 位置
    */
    private String sectionLocation;

    /**
    * 报警类型1:交通事件2:路面异物3:公路标志标线损坏4:安防设施损坏5:非法占用公路行为
    */
    private Integer type;

    /**
    * 报警内容
    */
    private String content;

    /**
    * 报警时间
    */
    private LocalDateTime alarmTime;

    /**
     * 报警位置
     */
    private String alarmLocation;

    /**
    * 报警参数
    */
    private String alarmParam;

    /**
     *算法编号
     */
    private Integer algorithmNum;

    /**
     * 算法名称
     */
    private String algorithmName;

    /**
     * 消息ID
     */
    private String msgId;

    /**
     * 状态 0.待核验1.已核验
     */
    private Integer status;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 是否误报 0.未误报1.误报
     */
    private Integer falseAlarm;

    /**
     * 工单状态1:未派单2:处理中3:已办结
     */
    private Integer worderStatus;

}
