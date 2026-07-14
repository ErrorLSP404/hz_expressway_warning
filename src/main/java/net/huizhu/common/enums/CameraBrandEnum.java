package net.huizhu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CameraBrandEnum {

    DH("DH","大华"), HK("HK","海康"),YS("YS","宇视");

    private String code;
    private String desc;
}
