package net.huizhu.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {

//    public static final String TOPIC_QUEUE_NAME = "alarmQueue"; //队列名称
//    public static final String TOPIC_EXCHANGE_NAME = "alarmExchange"; //交换器名称
//    public static final String TOPIC_ROUTING_KEY = "alarm"; //路由键
//
//    public static final String DIRECT_QUEUE_NAME = "highwayQueue"; //队列名称
//    public static final String DIRECT_EXCHANGE_NAME = "highway_exchange"; //交换器名称
//    public static final String DIRECT_ROUTING_KEY = "highway_key"; //路由键

    public static final String FANOUT_QUEUE_NAME = "highwayQueue"; //队列名称
    public static final String FANOUT_EXCHANGE_NAME = "highway_exchange"; //交换器名称

    // 延迟队列&交换机
    public static final String DELAYED_QUEUE_NAME = "delayedQueue";
    public static final String DELAYED_EXCHANGE_NAME = "delay_exchange";
    // 路由key
    public static final String DELAYED_ROUTING_KEY = "delayed.message";



//    @Bean("alarmQueue")
//    public Queue alarmQueue() {
//        return new Queue(TOPIC_QUEUE_NAME,true, false, false, null);
//    }
//
//    @Bean("topicExchange")
//    TopicExchange exchange() {
//        return new TopicExchange(TOPIC_EXCHANGE_NAME,true, false);
//    }
//
//    @Bean
//    public Binding fanoutABinding(@Qualifier("alarmQueue")Queue queue, TopicExchange topicExchange){
//        return BindingBuilder.bind(queue).to(topicExchange).with(TOPIC_ROUTING_KEY);
//    }
//
//    /**
//     * 声明直连交换机 支持持久化.
//     * @return the exchange
//     */
//    @Bean("directExchange")
//    DirectExchange directExchange() { return new DirectExchange(DIRECT_EXCHANGE_NAME,false,false);}
//
//    @Bean("directQueue")
//    public Queue directQueue(){
//        return new Queue(DIRECT_QUEUE_NAME, true, false, false,null);
//    }
//
//    @Bean
//    public Binding directBinding(@Qualifier("directQueue")Queue queue,DirectExchange directExchange){
//        return BindingBuilder.bind(queue).to(directExchange).with(DIRECT_ROUTING_KEY);
//    }

    /**
     * 声明广播交换机 支持持久化.
     * @return the exchange
     */
    @Bean("fanoutExchange")
    FanoutExchange fanoutExchange(){return new FanoutExchange(FANOUT_EXCHANGE_NAME,false,false);}

    @Bean("fanoutQueue")
    public Queue fanoutQueue(){
        return new Queue(FANOUT_QUEUE_NAME, true, false, false,null);
    }

    @Bean
    public Binding fanoutBinding(@Qualifier("fanoutQueue")Queue queue,FanoutExchange fanoutExchange){
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }


    /**
     *  此处设置，将对象以json的方式发送出去，存储在队列中
     *  若不配置默认是对象序列化以后发送出去，在rabbitmq web端看到队列中存储的是一串序列化后的乱码
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean("delayedExchange")
    CustomExchange delayedExchange() {
        Map<String,Object> map = new HashMap<>(1);
        map.put("x-delayed-type","direct");       //延迟队列类型，固定值

        return new CustomExchange(DELAYED_EXCHANGE_NAME,"x-delayed-message",
                true,false,map);

    }

    @Bean("delayQueue")
    public Queue delayQueue(){
        return QueueBuilder.durable(DELAYED_QUEUE_NAME).build();
    }

    /*绑定，自定义交换机绑定多一个 noargs方法 */
    @Bean
    public Binding delayBing(@Qualifier("delayQueue") Queue delayQueue,
                             @Qualifier("delayedExchange") CustomExchange delayedExchange){
        return BindingBuilder.bind(delayQueue).to(delayedExchange)
                .with(DELAYED_ROUTING_KEY)
                .noargs();
    }


}
