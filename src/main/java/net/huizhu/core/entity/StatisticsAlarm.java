package net.huizhu.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
*
* @author lsp
* @since 2021-12-21
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StatisticsAlarm implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
    * 创建时间
    */
    private LocalDateTime gmtCreate;

    /**
    * 修改时间
    */
    private LocalDateTime gmtModified;

    /**
    * 公路名称
    */
    private Long highwayId;

    /**
    * 公路名称
    */
    private String highwayName;

    /**
    * 路段ID
    */
    private Long sectionId;

    /**
    * 路段名称
    */
    private String sectionName;

    /**
    * 交通事件数量
    */
    private Integer trafficEvent;

    /**
    * 路面异物事件
    */
    private Integer pavementForeignMatter;

    /**
    * 公路标志标线损坏数量
    */
    private Integer signDamage;

    /**
    * 安防设施损坏数量
    */
    private Integer securityDamage;

    /**
    * 非法占用公路行为数量
    */
    private Integer illegalOccupation;


    /**
     * 统计日期
     */
    private LocalDateTime statisticsTime;

    /**
     * 年
     */
    private Integer year;

    /**
     * 月
     */
    private Integer month;

    /**
     * key
     */
    private String keyTime;
}
