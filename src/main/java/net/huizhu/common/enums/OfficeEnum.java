package net.huizhu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OfficeEnum {
    DAN_YANG(1,"丹阳公路中心"),DAN_TU(2,"丹徒公路中心"),
    BAO_RONG(3,"包容公路中心"),YANG_ZHONG(4,"扬中公路中心"),
    CITY(5,"市区公路中心"),THREE(6,"321公路中心");


    private long code;
    private String desc;

}
