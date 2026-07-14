package net.huizhu.controller.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.huizhu.controller.vo.AlarmLogVo;
import net.huizhu.controller.vo.AlgorithmVo;
import net.huizhu.controller.vo.CameraInfoVo;
import net.huizhu.core.entity.CameraInfo;

import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class AlarmLogResponse extends BaseResponse{

    private AlarmLogVo alarmLogVo;

    private List<AlarmLogVo> alarmLogVoList;

    private List<AlgorithmVo> algorithmVoList;

    private List<CameraInfoVo> cameraInfoVoList;

}
