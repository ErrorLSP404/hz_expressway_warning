package net.huizhu.controller.vo;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EventTrendVo {

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime time;

    private String timeView;

    private int trafficevent = 0;

    private int pavementForeignMatter = 0;

    private int signDamage = 0;

    private int securityDamage = 0;

    private int illegalOccupation = 0;

    public void setTime(LocalDateTime time) {
        this.time = time;
        this.timeView = LocalDateTimeUtil.format(time, DatePattern.NORM_DATE_PATTERN);
    }
}
