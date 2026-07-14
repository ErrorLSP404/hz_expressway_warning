package net.huizhu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkOrderEnum {

    STATUS(0,"未派单"),PROCESSING(1,"处理中"),FINISH(2,"结束");

    private Integer code;
    private String desc;

}
