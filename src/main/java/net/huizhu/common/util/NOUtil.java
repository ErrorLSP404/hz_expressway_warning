package net.huizhu.common.util;

import cn.hutool.core.util.RandomUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NOUtil {

    public static Long getWorkOrderNo() {
        return Long.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + RandomUtil.randomNumbers(2));
    }
}
