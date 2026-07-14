//package net.huizhu.rabbit;
//
//
//
//import com.rabbitmq.client.Channel;
//import lombok.extern.slf4j.Slf4j;
//import net.huizhu.common.config.RabbitConfig;
//import org.springframework.amqp.rabbit.annotation.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import org.springframework.messaging.handler.annotation.Headers;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//@Slf4j
//@Component
//public class RabbitTopicReceiver {
//
//    @Autowired
//    private RedisTemplate<String,Object> redisTemplate;
//    @Autowired
//    private RabbitSender rabbitSender;
//
//
//    private ExecutorService singleThreadExecutor;
//
//    {
//       this.singleThreadExecutor = Executors.newSingleThreadExecutor();
//    }
//
//
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = RabbitConfig.TOPIC_QUEUE_NAME,
//                    durable="true"),
//            exchange = @Exchange(value = RabbitConfig.TOPIC_EXCHANGE_NAME,
//                    durable="true",
//                    type= "topic",
//                    ignoreDeclarationExceptions = "true"),
//            key = RabbitConfig.TOPIC_ROUTING_KEY
//    )
//    )
//    @RabbitHandler
//    public void onMessage(@Payload Object object, Channel channel, @Headers Map<String, Object> headers) throws Exception {
//        System.out.println(object);
//    }
//}
