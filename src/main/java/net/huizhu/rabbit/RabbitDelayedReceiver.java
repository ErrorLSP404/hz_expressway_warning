package net.huizhu.rabbit;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import net.huizhu.common.enums.RevocationEnum;
import net.huizhu.common.enums.TimeOutEnum;
import net.huizhu.common.enums.WorkOrderEnum;
import net.huizhu.core.entity.WorkOrder;
import net.huizhu.core.service.IWorkOrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;


@Slf4j
@Component
public class RabbitDelayedReceiver {

    @Autowired
    private IWorkOrderService iWorkOrderService;

    @RabbitListener(queues = "delayedQueue")
    public void receiveDelayQueue(Message message, Channel channel, @Headers Map<String, Object> headers)throws Exception{

        String msg = new String(message.getBody());
        if (StrUtil.isNotBlank(msg)) {
            Long workNo = Long.valueOf(msg);
            // 获取工单  判断是否过期
            QueryWrapper<WorkOrder> workOrderQueryWrapper = new QueryWrapper<>();
            workOrderQueryWrapper.lambda().eq(WorkOrder::getWorkNo,workNo);
            WorkOrder workOrder = iWorkOrderService.getOne(workOrderQueryWrapper);
            if (workOrder != null) {
                if (workOrder.getStatus().equals(WorkOrderEnum.PROCESSING.getCode())) {
                    if (workOrder.getRevocation().equals(RevocationEnum.UN_REPEALED.getCode())) {
                        workOrder.setTimeOut(TimeOutEnum.TIMEOUT_YES.getCode());
                        iWorkOrderService.updateById(workOrder);
                    }
                }
            }else {
                log.warn("工单不存在，工单单号:{}",workNo);
            }
        }
        Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(deliveryTag,false);
        log.info("当前时间:{},收到死信队列的消息:{}",new Date(),msg);
    }

}
