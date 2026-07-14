package net.huizhu.controller.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class HighwayRequest extends BaseRequest{

    /**
     * 高速公路名称
     */
    private String highwayName;

    /**
     * 高速公路位置
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
    private Integer highwaySectionNum = 0;

}
