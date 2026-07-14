package net.huizhu.rabbit;


import net.huizhu.common.config.RabbitConfig;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RabbitSender {

    //自动注入RabbitTemplate模板类
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //回调函数: confirm确认
    final ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            System.err.println("correlationData: " + correlationData);
            System.err.println("ack: " + ack);
            System.err.println("cause: "+cause);
            if(!ack){
                System.err.println("异常处理....");
            }
        }
    };

    //回调函数: return返回
    final RabbitTemplate.ReturnsCallback returnsCallback = new RabbitTemplate.ReturnsCallback() {

        @Override
        public void returnedMessage(ReturnedMessage returned) {
            System.err.println("return exchange: " + returned.getExchange() + ", routingKey: "
                    + returned.getRoutingKey() + ", replyCode: " + returned.getReplyCode() + ", replyText: " + returned.getReplyText());
        }
    };

//    //发送消息方法调用: 构建Message消息(Topic)
//    public void sendTopic(Object message) throws Exception {
//        rabbitTemplate.setConfirmCallback(confirmCallback);
//        rabbitTemplate.setReturnsCallback(returnsCallback);
//        CorrelationData correlationData = new CorrelationData();
//        rabbitTemplate.convertAndSend(RabbitConfig.TOPIC_EXCHANGE_NAME, RabbitConfig.TOPIC_ROUTING_KEY, message,correlationData);
//    }
//
//    //发送消息方法调用: 构建Message消息(direct)
//    public void sendDirect(Object message) throws Exception {
//        rabbitTemplate.setConfirmCallback(confirmCallback);
//        rabbitTemplate.setReturnsCallback(returnsCallback);
//        CorrelationData correlationData = new CorrelationData();
//        rabbitTemplate.convertAndSend(RabbitConfig.DIRECT_EXCHANGE_NAME, RabbitConfig.DIRECT_ROUTING_KEY, message,correlationData);
//    }


    //发送消息方法调用: 构建Message消息(direct)
    public void sendFanout(Object message) throws Exception {
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnsCallback(returnsCallback);
        CorrelationData correlationData = new CorrelationData();
        rabbitTemplate.convertAndSend(RabbitConfig.FANOUT_EXCHANGE_NAME,"", message,correlationData);
    }


}

