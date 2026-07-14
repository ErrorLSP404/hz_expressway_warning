package net.huizhu.controller.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class BaseRequest {

    private Long id;

    private Integer pageNo = 1;

    private Integer pageSize = 10;
}
