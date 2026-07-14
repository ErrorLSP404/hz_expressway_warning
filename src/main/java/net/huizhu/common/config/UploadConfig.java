package net.huizhu.common.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadConfig {

    //文件服务器地址
    public  static String fileUrl;

    public void setFileUrl(String fileUrl) {
        UploadConfig.fileUrl = fileUrl;
    }
}
