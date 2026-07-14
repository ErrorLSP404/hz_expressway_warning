package net.huizhu.controller.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HighWayVo {

    /**
     * id
     */
    private Long id;

    /**
     * 公路名称
     */
    private String highwayName;

    /**
     * 位置
     */
    private String highwayLocation;

    /**
     * 地图坐标
     */
    private String highwayMapCoordinate;

    /**
     * 简介
     */
    private String highwayIntroduction;

    /**
     * 路段数量
     */
    private Integer highwaySectionNum;

}
