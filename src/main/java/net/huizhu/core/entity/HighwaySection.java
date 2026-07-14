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
public class HighwaySection implements Serializable {

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
