package com.cheney.seckill.mq.rabbitmq;

import com.cheney.seckill.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {


    @Autowired
    private RabbitTemplate rabbitTemplate;
    public void sendMQ(String msg){
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,RabbitMQConfig.ROUTING_KEY,msg);
    }
}
