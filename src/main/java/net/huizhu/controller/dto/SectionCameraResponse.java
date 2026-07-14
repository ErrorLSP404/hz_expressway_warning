package net.huizhu.controller.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.huizhu.controller.vo.RlSectionCameraVo;

import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SectionCameraResponse extends BaseResponse{

    private List<RlSectionCameraVo> sectionCameraVoList;

}
