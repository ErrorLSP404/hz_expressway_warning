package net.huizhu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkQuickEnum {

    SLOW(0,"非快速办结"),QUICK(1,"快速办结");

    private Integer code;
    private String desc;
}
