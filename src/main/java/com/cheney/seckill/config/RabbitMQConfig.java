package com.cheney.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "seckill_exchanger";
    public static final String ROUTING_KEY = "seckill.order";
    public static final String QUEUE_NAME = "seckill_queue";
    //1交换机
    @Bean
    public Exchange exchange(){
        return new TopicExchange(EXCHANGE_NAME);
    }
    //2队列
    @Bean
    public Queue queue(){
        return new Queue(QUEUE_NAME);
    }
    //3绑定
    @Bean
    public Binding binding(Exchange exchange,Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with("seckill.#").noargs();
    }
}
