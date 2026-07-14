package net.huizhu.controller.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.huizhu.controller.vo.HighWayVo;
import net.huizhu.controller.vo.HighwaySectionVo;

import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class HighwayResponse extends BaseResponse{

    private HighWayVo highWayVo;

    private HighwaySectionVo highwaySectionVo;

    private List<HighWayVo> highWayVoList;

    private List<HighwaySectionVo> highwaySectionVoList;
}
