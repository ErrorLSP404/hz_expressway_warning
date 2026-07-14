package net.huizhu.controller.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class DetectionRequest extends BaseRequest{

    /**
     * 消息ID
     */
    private String msgId;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 摄像头设备序列号
     */
    private String deviceId;

    /**
     * 摄像头通道号
     */
    private String channelId;

    /**
     * 摄像头位置
     */
    private String position;

    /**
     * 抓拍图片访问地址
     */
    private String captureImage;

    /**
     * 抓拍短视频访问地址
     */
    private String videoSrc;

    /**
     * 报警时间
     */
    private String alarmTime;

    /**
     * 报警位置
     */
    private String alarmPosition;

    /**
     * 报警信息
     */
    private String alarmMsg;

    /**
     *算法编号
     */
    private Integer algorithmNum;

    /**
     * 算法名称
     */
    private String algorithmName;

}
