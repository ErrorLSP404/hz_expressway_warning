package net.huizhu.rabbit.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HighWayAssist {

    /**
     * 公路ID
     */
    private Long highwayId;

    /**
     * 公路名称
     */
    private String highwayName;

    /**
     * 公路路段
     */
    private SectionAssist  sectionAssist;


}
