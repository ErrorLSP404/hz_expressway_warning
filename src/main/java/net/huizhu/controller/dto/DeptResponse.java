package net.huizhu.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.huizhu.controller.vo.DeptVo;
import net.huizhu.controller.vo.OfficeVo;

import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeptResponse {

    private List<DeptVo> deptVoList;

    private List<OfficeVo> officeVoList;
}
