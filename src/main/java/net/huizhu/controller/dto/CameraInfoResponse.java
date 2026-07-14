package net.huizhu.controller.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.huizhu.controller.vo.CameraBrandVo;
import net.huizhu.controller.vo.CameraInfoVo;

import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class CameraInfoResponse extends BaseResponse{

    private CameraInfoVo cameraInfoVo;

    private List<CameraInfoVo> cameraInfoVoList;

    private List<CameraBrandVo> cameraBrandVoList;
}
