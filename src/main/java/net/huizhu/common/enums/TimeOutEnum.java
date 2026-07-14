package net.huizhu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimeOutEnum {

    TIMEOUT_NO(0,"未超时"),TIMEOUT_YES(1,"已超时");

    private Integer code;
    private String desc;
}
