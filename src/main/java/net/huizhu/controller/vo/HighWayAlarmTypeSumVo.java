package net.huizhu.controller.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HighWayAlarmTypeSumVo {

    private Long highWayId;

    private String highWayName;

    private int trafficevent = 0;

    private int pavementForeignMatter = 0;

    private int signDamage = 0;

    private int securityDamage = 0;

    private int illegalOccupation = 0;

    private int sum = 0;

    public void setTrafficevent(int trafficevent) {
        if (trafficevent != 0){
            this.trafficevent = trafficevent;
            this.sum = sum+trafficevent;
        }
    }

    public void setPavementForeignMatter(int pavementForeignMatter) {
        if(pavementForeignMatter != 0){
            this.pavementForeignMatter = pavementForeignMatter;
            this.sum = sum+pavementForeignMatter;
        }
    }

    public void setSignDamage(int signDamage) {
        if(signDamage != 0){
            this.signDamage = signDamage;
            this.sum = sum+signDamage;
        }
    }

    public void setSecurityDamage(int securityDamage) {
        if(securityDamage != 0){
            this.securityDamage = securityDamage;
            this.sum = sum+securityDamage;
        }
    }

    public void setIllegalOccupation(int illegalOccupation) {
        if(illegalOccupation != 0){
            this.illegalOccupation = illegalOccupation;
            this.sum = sum+illegalOccupation;
        }
    }
}
