package net.huizhu.controller.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SectionResquest extends BaseRequest {

    /**
     * 高速公路Id
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
