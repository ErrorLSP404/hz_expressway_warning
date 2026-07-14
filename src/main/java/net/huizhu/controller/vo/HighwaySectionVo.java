package net.huizhu.controller.vo;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HighwaySectionVo {

    private Long id;

    /**
     * 高速公路编号
     */
    private Long highwayId;

    /**
     * 编号
     */
    private Integer sectionNum;

    /**
     * 路段名称
     */
    private String sectionName;

    /**
     * 路段公里数
     */
    private Long sectionKm;

    /**
     * 位置
     */
    private String sectionLocation;
}
