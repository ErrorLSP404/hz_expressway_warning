package net.huizhu.controller.vo;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RlSectionCameraVo {

    private Long id;

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
