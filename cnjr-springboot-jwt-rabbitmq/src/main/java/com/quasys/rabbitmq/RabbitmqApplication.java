package com.quasys.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootApplication
public class RabbitmqApplication {

  static final String topicExchangeName = "spring-boot-exchange";

  static final String queueName = "spring-boot";
  static final String queueHost = "rabbitmqcluster-quasys.rabbitmq-system.svc.cluster.local";
  static final Integer queuePort = 5672;
  static final String queueUsername = "guest";
  static final String queuePassword = "guest";


  @Bean
  Queue queue() {
    return new Queue(queueName, false);
  }

  @Bean
  TopicExchange exchange() {
    return new TopicExchange(topicExchangeName);
  }

  @Bean
  Binding binding(Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
  }

  @Bean
  SimpleMessageListenerContainer container(CachingConnectionFactory connectionFactory,
      MessageListenerAdapter listenerAdapter) {
    System.out.println("Hola4");
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    final CachingConnectionFactory connectionFactory2 = new CachingConnectionFactory();
    connectionFactory2.setUsername(queueUsername);
    connectionFactory2.setPassword(queuePassword);
    connectionFactory2.setHost(queueHost);
    connectionFactory2.setPort(queuePort);
    connectionFactory2.setVirtualHost(queueName);
    System.out.println("Hola5");
    connectionFactory2.createConnection();
    System.out.println("Hola6");
    container.setConnectionFactory(connectionFactory2);
    System.out.println("Hola7");
    container.setQueueNames(queueName);
    System.out.println("Hola8");
    container.setMessageListener(listenerAdapter);
    System.out.println("Hola9");
    return container;
  }

  @Bean
  MessageListenerAdapter listenerAdapter(Receiver receiver) {
    System.out.println("Hola3");
    return new MessageListenerAdapter(receiver, "receiveMessage");
  }

  public static void main(String[] args) throws InterruptedException {
    System.out.println("Hola1");
    SpringApplication.run(RabbitmqApplication.class, args).close();
    System.out.println("Hola2");
  }

}