package net.huizhu.controller.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StatisticsEventVo {

    private Integer cameraNum = 0;

    private Integer todayEventNum = 0;

    private Integer monthEventNum = 0;

    private Integer yearEventNum = 0;
}
