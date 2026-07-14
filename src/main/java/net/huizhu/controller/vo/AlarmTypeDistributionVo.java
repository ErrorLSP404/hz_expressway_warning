package net.huizhu.controller.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AlarmTypeDistributionVo {

    private int trafficevent = 0;

    private int pavementForeignMatter = 0;

    private int signDamage = 0;

    private int securityDamage = 0;

    private int illegalOccupation = 0;

}
