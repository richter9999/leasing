package com.yhzt.leasing.config;

import javax.annotation.Resource;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RabbitMQConfig {
    public final static String QUEUE_NAME = "spring-boot-queue";
    public final static String QUEUE_NAME_SELECT = "db.select";
    public final static String QUEUE_NAME_INSERT = "db.insert";
    public final static String QUEUE_NAME_UPDATE = "db.update";
    public final static String QUEUE_NAME_DELETE = "db.delete";
    public final static String QUEUE_NAME_EMAIL = "spring-boot-queue-email";
    public final static String QUEUE_NAME_TRANSACTION = "spring-boot-queue-transaction";

    public final static String EXCHANGE_NAME = "spring-boot-exchange";
    public final static String EXCHANGE_SELECT = "change.select";
    public final static String EXCHANGE_INSERT = "change.insert";
    public final static String EXCHANGE_UPDATE = "change.update";
    public final static String EXCHANGE_DELETE = "change.delete";
    public final static String EXCHANGE_EMAIL = "change.email";
    public final static String EXCHANGE_TRANSACTION = "change.transaction";

    public final static String ROUTING_KEY = "spring-boot-key";
    public final static String ROUTING_KEY_SELECT = "key.db.select";
    public final static String ROUTING_KEY_INSERT = "key.db.insert";
    public final static String ROUTING_KEY_UPDATE = "key.db.update";
    public final static String ROUTING_KEY_DELETE = "key.db.delete";
    public final static String ROUTING_KEY_EMAIL = "spring-boot-key-email";
    public final static String ROUTING_KEY_TRANSACTION = "spring-boot-key-transaction";

    @Resource
    private CachingConnectionFactory connectionFactory;

    //创建队列
    @Bean
    public Queue queue(){
        return new Queue(QUEUE_NAME);
    }

    //创建队列
    /* @Bean
    public Queue queueSelect(){
        return new Queue(QUEUE_NAME_SELECT);
    } */

    //创建一个 topic 类型的交换器
    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGE_NAME);
    }

    //创建一个 topic 类型的交换器
    /* @Bean
    public TopicExchange exchangeSelect(){
        return new TopicExchange(EXCHANGE_SELECT);
    } */

    //使用路由键（routingKey）把队列（Queue）绑定到交换器（Exchange）
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    //使用路由键（routingKey）把队列（Queue）绑定到交换器（Exchange）
    /* @Bean
    public Binding bindingSelect(Queue queueSelect, TopicExchange exchangeSelect){
        return BindingBuilder.bind(queueSelect).to(exchangeSelect).with(ROUTING_KEY_SELECT);
    } */

    /* @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("127.0.0.1", 5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    } */

    /* @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        return new RabbitTemplate(connectionFactory);
    } */

    @Bean
    public RabbitTemplate rabbitTemplate(){
        //若使用confirm-callback或return-callback，必须要配置publisherConfirms或publisherReturns为true
        //每个rabbitTemplate只能有一个confirm-callback和return-callback，如果这里配置了，那么写生产者的时候不能再写confirm-callback和return-callback
        //使用return-callback时必须设置mandatory为true，或者在配置中设置mandatory-expression的值为true
        //connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);

//        /**
//         * 前面设置的setPublisherConfirms是解决消息发送到交换机,生产者的问题
//         * 如果消息没有到exchange,则confirm回调,ack=false
//         * 如果消息到达exchange,则confirm回调,ack=true
//         * 前面设置的setPublisherReturns是交换机发送到队列的问题,交换机的问题
//         * exchange到queue成功,则不回调return
//         * exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了)
//         */
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if(ack){
                    log.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
                }else{
                    log.info("消息发送失败:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
                }
            }
        }); 

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",exchange,routingKey,replyCode,replyText,message);
            }
        });
        return rabbitTemplate;
    }
}