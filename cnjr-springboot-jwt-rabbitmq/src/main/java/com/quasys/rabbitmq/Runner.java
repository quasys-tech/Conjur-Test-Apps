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

  public Runner(Receiver receiver, RabbitTemplate rabbitTemplate) {
    this.receiver = receiver;
    this.rabbitTemplate = rabbitTemplate;

  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("Sending message...");
    int i = 1;
    while (true) {
      rabbitTemplate.convertAndSend(RabbitmqApplication.topicExchangeName, "foo.bar.baz", "Hello from RabbitMQ!" + i);
      receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
      i = i + 1;
      Thread.sleep(5000);
    }

  }

}