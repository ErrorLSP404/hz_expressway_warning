package net.huizhu.controller.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class AlarmLogRequest extends BaseRequest{

    private Long cameraId;

    private List<Long> alarmLogIds;

    private Long highwayId;

    private Long sectionId;

    private Integer algorithmNum;

    /**
     * 误报 0:未误报 1:误报
     */
    private Integer falseAlarm = 0;

    private String remarks;

    /**
     * 0:误报 1:交通事件2:路面异物3:公路标志标线损坏4:安防设施损坏5:非法占用公路行为
     */
    private Integer alarmType;

    /**
     * 状态 0.待核验1.已核验
     */
    private Integer status;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

}
