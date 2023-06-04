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

@SpringBootApplication
public class RabbitmqApplication {

  static final String topicExchangeName = "spring-boot-exchange";

  static final String queueName = "spring-boot";
  static final String queueHost = "rabbitmqcluster-quasys.rabbitmq-system.svc.cluster.local";
  static final Integer queuePort = 5672;
  static final String queueUsername = "guest";
  static final String queuePassword = "guest";

  public static final String BROKER_HOSTNAME = "RABBITMQ_TEST_HOSTNAME";
  public static final String BROKER_PORT = "RABBITMQ_TEST_PORT";
  public static final String BROKER_USER = "RABBITMQ_TEST_USER";
  public static final String BROKER_PW = "RABBITMQ_TEST_PASSWORD";
  public static final String DEFAULT_QUEUE_NAME = "RABBITMQ_TEST_ADMIN_USER";

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
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
	
    connectionFactory.setUsername(queueUsername);
    connectionFactory.setPassword(queuePassword);
    connectionFactory.setHost(queueHost);
    connectionFactory.setPort(queuePort);
    connectionFactory.setVirtualHost(queueName);
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(queueName);
    container.setMessageListener(listenerAdapter);
    return container;
  }

  @Bean
  MessageListenerAdapter listenerAdapter(Receiver receiver) {
    return new MessageListenerAdapter(receiver, "receiveMessage");
  }

  public static void main(String[] args) throws InterruptedException {
    SpringApplication.run(RabbitmqApplication.class, args).close();
  }

}