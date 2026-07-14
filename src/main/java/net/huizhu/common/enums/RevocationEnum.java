package net.huizhu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RevocationEnum {

    UN_REPEALED(0,"未撤销"),UN_DONE(1,"已撤销");

    private Integer code;
    private String desc;

}
