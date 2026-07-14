package net.huizhu.controller.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticsVo {

    // 未处理
    private Long untreatedCount;

    // 已处理
    private Long processedCount;

    // 已结办
    private Long finishCount;

    // 已超时
    private Long timeOut;

    // 月份
    private String dateTime;

    // 丹阳公路中心
    private Long danYang;

    // 丹徒公路中心
    private Long danTu;

    // 包容公路中心
    private Long pardon;

    // 扬中公路中心
    private Long yangZhong;

    // 市区公路中心
    private Long city;

    // 321公路中心
    private Long threeTwoOne;

    // 时间排序
    private LocalDateTime localDateTime;

    // 无事件发生
    private Long nothing;

    // 无事件发生耗时
    private String nothingTime;

    // 交通事件
    private Long trafficEvent;

    // 交通事件耗时
    private String trafficEventTime;

    // 路面异物
    private Long pavementForeignMatter;

    // 路面异物耗时
    private String pavementForeignMatterTime;

    // 公路标志标线损坏
    private Long signDamage;

    // 公路标志标线损坏耗时
    private String signDamageTime;

    // 安防设施损坏
    private Long securityDamage;

    // 安防设施损坏耗时
    private String securityDamageTime;

    // SecurityDamage
    private Long illegalOccupation;

    // SecurityDamage耗时
    private String illegalOccupationTime;

    // 耗时
    private String elapsedTime;

    // 是否有值
    private Boolean hasWorkOrder;

}
