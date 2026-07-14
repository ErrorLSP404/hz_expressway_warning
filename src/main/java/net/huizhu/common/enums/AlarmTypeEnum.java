package net.huizhu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmTypeEnum {

    NOTHING(0,"无事件发生"),
    TrafficEvent(1,"交通事件"),PavementForeignMatter(2,"路面异物"),
    SignDamage(3,"公路标志标线损坏"),SecurityDamage(4,"安防设施损坏"),
    IllegalOccupation(5,"非法占用公路行为");

    private Integer code;
    private String desc;

}
