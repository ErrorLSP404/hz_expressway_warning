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
//public class RabbitDirectReceiver {
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = RabbitConfig.DIRECT_QUEUE_NAME,
//                    durable="true"),
//            exchange = @Exchange(value = RabbitConfig.DIRECT_EXCHANGE_NAME,
//                    durable="false",
//                    type= "direct",
//                    ignoreDeclarationExceptions = "true"),
//            key = RabbitConfig.DIRECT_ROUTING_KEY
//    )
//    )
//    @RabbitHandler
//    public void onMessage(@Payload CollisionMsg collisionMsg, Channel channel, @Headers Map<String, Object> headers) throws Exception {
//        System.err.println("direct==========接收消息==============");
//        System.err.println(collisionMsg);
//        System.err.println("direct====================================");
//        Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
//        //手工ACK
//        channel.basicAck(deliveryTag, false);
//    }
//}
