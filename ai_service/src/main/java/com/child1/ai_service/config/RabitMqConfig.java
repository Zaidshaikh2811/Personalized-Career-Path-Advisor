package com.child1.ai_service.config;




import org.springframework.amqp.core.*;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabitMqConfig {


    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.queue.name}")
    private String queue;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Value("${rabbitmq.update.queue.name}")
    private String updateQueue;

    @Value("${rabbitmq.update.routing.key}")
    private String updateRoutingKey;

    @Value("${rabbitmq.delete.queue.name}")
    private String deleteQueue;

    @Value("${rabbitmq.delete.routing.key}")
    private String deleteRoutingKey;


    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }


    @Bean
    public Queue activityQueue() {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    public Queue activityUpdateQueue() {
        return QueueBuilder.durable(updateQueue).build();
    }

    @Bean
    public Queue activityDeleteQueue() {
        return QueueBuilder.durable(deleteQueue).build();
    }


    @Bean
    public Binding activityBinding() {
        return BindingBuilder
                .bind(activityQueue())
                .to(exchange())
                .with(routingKey);
    }

    @Bean
    public Binding activityUpdateBinding() {
        return BindingBuilder
                .bind(activityUpdateQueue())
                .to(exchange())
                .with(updateRoutingKey);
    }

    @Bean
    public Binding activityDeleteBinding() {
        return BindingBuilder
                .bind(activityDeleteQueue())
                .to(exchange())
                .with(deleteRoutingKey);
    }


    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
