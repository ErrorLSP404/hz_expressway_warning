package net.huizhu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlgorithmEnum {

    TrafficEvent(1,"交通事件监测智能识别算法"),PavementForeignMatter(2,"路面异物智能识别算法"),
    SignDamage(3,"公路标志标线损坏智能识别算法"),SecurityDamage(4,"安防设施损坏智能识别算法"),
    IllegalOccupation(5,"非法占用公路行为智能识别算法");

    private Integer code;
    private String desc;

}
