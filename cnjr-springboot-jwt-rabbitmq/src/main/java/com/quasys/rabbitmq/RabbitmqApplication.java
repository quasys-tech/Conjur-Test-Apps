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
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import com.rabbitmq.client.ConnectionFactory;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.time.Clock;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class RabbitmqApplication {

  static final String topicExchangeName = "spring-boot-exchange";

  static final String queueName = "spring-boot";
  static final String queueHost = "rabbitmqcluster-quasys.rabbitmq-system.svc.cluster.local";
  static final Integer queuePort = 5672;
  static final String queueUsername = "abdulmelik";
  static final String queuePassword = "Hey1234";


  @Bean
  Queue queue() {
    return new Queue("springhost", false);
  }

  @Bean
  TopicExchange exchange() {
    return new TopicExchange("spring-boot-exchange");
  }

  @Bean
  Binding binding(Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
  }

//  @Bean
//  SimpleMessageListenerContainer container(CachingConnectionFactory connectionFactory,
//      MessageListenerAdapter listenerAdapter) {
//    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//    final CachingConnectionFactory connectionFactory2 = new CachingConnectionFactory();
//    connectionFactory2.setUsername(queueUsername);
//    connectionFactory2.setPassword(queuePassword);
//    connectionFactory2.setHost(queueHost);
//    connectionFactory2.setPort(queuePort);
//    connectionFactory2.setVirtualHost(queueName);
//    connectionFactory2.createConnection();
//    container.setConnectionFactory(connectionFactory2);
//    container.setQueueNames(queueName);
//    container.setMessageListener(listenerAdapter);
//    return container;
//  }
  @Bean
  SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                           MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames("springhost");
    container.setMessageListener(listenerAdapter);
    return container;
  }
  @Bean
  MessageListenerAdapter listenerAdapter(Receiver receiver) {
    return new MessageListenerAdapter(receiver, "receiveMessage");
  }

  public static void main(String[] args) throws InterruptedException, IOException, TimeoutException {
    System.out.println("Hola1");
//    ConnectionFactory factory = new ConnectionFactory();
//    factory.setHost(queueHost);
//    factory.setVirtualHost("spring-boot");
//    factory.setUsername(queueUsername);
//    factory.setPassword(queuePassword);
//    factory.setPort(queuePort);
//    try (Connection connection = factory.newConnection();
//         Channel channel = connection.createChannel()) {
//      channel.
//      channel.queueDeclare("spring-boot", false, false, false, null);
//      String message = "Hello World! Abdulmelik";
//      channel.basicPublish("", "spring-boot", null, message.getBytes());
//      System.out.println(" [x] Sent '" + message + "'");
//    }


    SpringApplication.run(RabbitmqApplication.class, args).close();
    System.out.println("Hola2");
  }

}