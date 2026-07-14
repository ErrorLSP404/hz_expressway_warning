package net.huizhu.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
* @since 2021-11-30
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
    public class Highway implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
