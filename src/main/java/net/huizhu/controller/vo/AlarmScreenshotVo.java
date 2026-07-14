package net.huizhu.controller.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AlarmScreenshotVo {

    private Long id;

    /**
     * 警报ID
     */
    private Long alarmId;

    /**
     * 报警图片地址
     */
    private String url;

    /**
     * 类型1.图片2.视频
     */
    private Integer type;

}
