package com.quasys.rabbitmq;

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

  private final RabbitTemplate rabbitTemplate;
  private final Receiver receiver;

  @Bean
  ConnectionFactory fooConn() {
    CachingConnectionFactory ccf = new CachingConnectionFactory("rabbitmqcluster-quasys.rabbitmq-system.svc.cluster.local", 5672);
    ccf.setVirtualHost("springhost");
    ccf.setPassword("Hey1234");
    ccf.setUsername("abdulmelik");
    return ccf;
  }
  public Runner(Receiver receiver, RabbitTemplate rabbitTemplate) {
    this.receiver = receiver;
    this.rabbitTemplate = rabbitTemplate;
    this.rabbitTemplate.setConnectionFactory(fooConn());

  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("Sending message...");

    rabbitTemplate.convertAndSend(RabbitmqApplication.topicExchangeName, "foo.bar.baz", "Hello from RabbitMQ!");
    receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
  }

}