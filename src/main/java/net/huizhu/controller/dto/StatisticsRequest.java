package net.huizhu.controller.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class StatisticsRequest extends BaseResponse{

    private String startTime;

    private String endTime;

    private String reviseDate;

    private Integer algorithmNum = 0;

}
