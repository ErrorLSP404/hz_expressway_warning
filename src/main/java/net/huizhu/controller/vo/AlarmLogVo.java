package net.huizhu.controller.vo;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.huizhu.rabbit.entity.AlarmParameter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AlarmLogVo {


    private Long id;

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
     * 报警类型 1:交通事件2:路面异物3:公路标志标线损坏4:安防设施损坏5:非法占用公路行为
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
     *算法编号
     */
    private Integer algorithmNum;

    /**
     * 算法名称
     */
    private String algorithmName;


    /**
     * 报警参数对象
     */
    private AlarmParameter alarmParameter;

    /**
     * 碰撞截图
     */
    private List<AlarmScreenshotVo> screenshotList;

    /**
     * 报警时间显示
     */
    private String alarmTimeView;

    /**
     * 状态 0.待核验1.已核验
     */
    private Integer status;

    /**
     * 备注
     */
    private String remarks  = "";

    /**
     * 是否误报 0.未误报1.误报
     */
    private Integer falseAlarm;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 工单状态:未派单0:处理中1:已办结2
     */
    private Integer worderStatus;

    public void setRemarks(String remarks) {
        if(StrUtil.isNotBlank(remarks)){
            this.remarks = remarks;
        }
    }

    public void setAlarmTime(LocalDateTime alarmTime) {
        this.alarmTime = alarmTime;
        this.alarmTimeView = LocalDateTimeUtil.format(alarmTime, DatePattern.NORM_DATETIME_PATTERN);
    }
}
