package net.huizhu.controller.vo;

import cn.hutool.core.util.NumberUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StatisticsTodayRealTimeVo {

    private Integer totality = 0;

    private Integer verified = 0;

    private String verifiedPercent = "0%";

    private Integer notVerified = 0;

    private String notVerifiedPercent = "0%";

    private Integer trafficEventNum = 0;

    private String trafficEventNumPercent = "0%";

    private Integer pavementForeignMatterNum = 0;

    private String pavementForeignMatterPercent = "0%";

    private Integer signDamageNum = 0;

    private String signDamagePercent = "0%";

    private Integer securityDamageNum = 0;

    private String securityDamagePercent = "0%";

    private Integer illegalOccupationNum = 0;

    private String illegalOccupationPercent = "0%";

    private List<AlarmLogVo> alarmLogVoList;


    public void setTrafficEventNum(Integer trafficEventNum) {
        this.trafficEventNum = trafficEventNum;
        if(trafficEventNum != null && totality != 0){
            BigDecimal div = NumberUtil.div(trafficEventNum, totality);
            this.trafficEventNumPercent = NumberUtil.decimalFormat("#.##%",div);
        }
    }

    public void setPavementForeignMatterNum(Integer pavementForeignMatterNum) {
        this.pavementForeignMatterNum = pavementForeignMatterNum;
        if(pavementForeignMatterNum != null && totality != 0){
            BigDecimal div = NumberUtil.div(pavementForeignMatterNum, totality);
            this.pavementForeignMatterPercent = NumberUtil.decimalFormat("#.##%",div);
        }
    }

    public void setSignDamageNum(Integer signDamageNum) {
        this.signDamageNum = signDamageNum;
        if(signDamageNum != null && totality != 0){
            BigDecimal div = NumberUtil.div(signDamageNum, totality);
            this.signDamagePercent = NumberUtil.decimalFormat("#.##%",div);
        }
    }

    public void setSecurityDamageNum(Integer securityDamageNum) {
        this.securityDamageNum = securityDamageNum;
        if(securityDamageNum != null && totality != 0){
            BigDecimal div = NumberUtil.div(securityDamageNum, totality);
            this.securityDamagePercent = NumberUtil.decimalFormat("#.##%",div);
        }
    }

    public void setIllegalOccupationNum(Integer illegalOccupationNum) {
        this.illegalOccupationNum = illegalOccupationNum;
        if(illegalOccupationNum != null && totality != 0){
            BigDecimal div = NumberUtil.div(illegalOccupationNum, totality);
            this.illegalOccupationPercent = NumberUtil.decimalFormat("#.##%",div);;
        }
    }

    public void setVerified(Integer verified) {
        this.verified = verified;
        if(verified != null && totality != 0 ){
            BigDecimal div = NumberUtil.div(verified, totality);
            this.verifiedPercent = NumberUtil.decimalFormat("#.##%",div);;
        }
    }

    public void setNotVerified(Integer notVerified) {
        this.notVerified = notVerified;
        if(notVerified != null && totality != 0 ){
            BigDecimal div = NumberUtil.div(notVerified, totality);
            this.notVerifiedPercent = NumberUtil.decimalFormat("#.##%",div);;
        }
    }
}
