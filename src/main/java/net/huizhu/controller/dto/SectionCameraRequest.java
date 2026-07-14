package net.huizhu.controller.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SectionCameraRequest extends BaseRequest{

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
