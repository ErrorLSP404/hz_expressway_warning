package net.huizhu;

import net.huizhu.core.service.InitDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;


@SpringBootApplication
@EnableScheduling
public class HzExpresswayWarningApplication {

    public static void main(String[] args) {
        SpringApplication.run(HzExpresswayWarningApplication.class, args);
    }

    @Autowired
    private InitDataService initDataService;

    @PostConstruct
    public void init(){
        //载入摄像头参数
        initDataService.initCamera();
    }

}
