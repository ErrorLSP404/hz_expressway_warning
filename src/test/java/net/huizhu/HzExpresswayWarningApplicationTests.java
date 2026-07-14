package net.huizhu;

import net.huizhu.rabbit.RabbitSender;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
class HzExpresswayWarningApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private RabbitSender rabbitSender;

    @Test
    public void testSender() throws Exception {

    }
}
