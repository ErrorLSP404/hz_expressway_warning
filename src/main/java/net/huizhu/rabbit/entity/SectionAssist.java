package net.huizhu.rabbit.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SectionAssist {

    /**
     * 路段ID
     */
    private Long sectionid;

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
