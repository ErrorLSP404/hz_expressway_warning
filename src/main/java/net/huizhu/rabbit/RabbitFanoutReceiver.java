//package net.huizhu.rabbit;
//
//
//import com.rabbitmq.client.Channel;
//import net.huizhu.common.config.RabbitConfig;
//import net.huizhu.rabbit.entity.CollisionMsg;
//import org.springframework.amqp.rabbit.annotation.*;
//import org.springframework.amqp.support.AmqpHeaders;
//import org.springframework.messaging.handler.annotation.Headers;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
//@Component
//public class RabbitFanoutReceiver {
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = RabbitConfig.FANOUT_QUEUE_NAME,
//                    durable="true"),
//            exchange = @Exchange(value = RabbitConfig.FANOUT_EXCHANGE_NAME,
//                    durable="false",
//                    type= "fanout",
//                    ignoreDeclarationExceptions = "true")
//    )
//    )
//    @RabbitHandler
//    public void onMessage(@Payload CollisionMsg collisionMsg, Channel channel, @Headers Map<String, Object> headers) throws Exception {
//        System.err.println("FANOUT==========接收消息==============");
//        System.err.println(collisionMsg);
//        System.err.println("FANOUT====================================");
//        //Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
//        //手工ACK
//        //channel.basicAck(deliveryTag, false);
//    }
//}
