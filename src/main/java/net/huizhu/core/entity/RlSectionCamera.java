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
    public class RlSectionCamera implements Serializable {

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
     * 高速公路ID
     */
    private Long highwayId;

    /**
     * 路段ID
     */
    private Long sectionId;

    /**
     * 路段名称
     */
    private String sectionName;

    /**
     * 摄像头ID
     */
    private Long cameraId;

    /**
     * 摄像头名称
     */
    private String cameraName;


}
