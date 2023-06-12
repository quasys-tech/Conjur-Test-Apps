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

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.time.Clock;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class RabbitmqApplication {

  static final String topicExchangeName = "spring-boot-exchange";

  static final String queueName = "springhost";
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

  @Bean
  ConnectionFactory fooConn() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
    CachingConnectionFactory ccf = new CachingConnectionFactory(queueHost, 5672);
    ccf.setVirtualHost(queueName);
    ccf.setPassword(getPassword());
    ccf.setUsername("abdulmelik");
    return ccf;
  }
  @Bean
  SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                           MessageListenerAdapter listenerAdapter) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(fooConn());
    container.setQueueNames("springhost");
    container.setMessageListener(listenerAdapter);
    return container;
  }
  @Bean
  MessageListenerAdapter listenerAdapter(Receiver receiver) {
    return new MessageListenerAdapter(receiver, "receiveMessage");
  }

  public static void main(String[] args) throws InterruptedException, IOException, TimeoutException {
    SpringApplication.run(RabbitmqApplication.class, args).close();
  }
  public static String getPassword() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, InterruptedException {

    String conjurCert = System.getenv("CONJUR_SSL_CERTIFICATE");
    String baseURL = System.getenv("CONJUR_APPLIANCE_URL");
    String conjurAccount = System.getenv("CONJUR_ACCOUNT");
    String secretPath = System.getenv("CONJUR_SECRET_ID");
    String serviceID = System.getenv("CONJUR_AUTHN_JWT_SERVICE_ID");
    Path filePath = Path.of("/var/run/secrets/kubernetes.io/serviceaccount/token");
    String tokenContent = Files.readString(filePath);

    final CertificateFactory cf = CertificateFactory.getInstance("X.509");
    final Certificate cert = cf.generateCertificate(new ByteArrayInputStream(conjurCert.getBytes()));
    final KeyStore ks = KeyStore.getInstance("JKS");
    ks.load(null);
    ks.setCertificateEntry("conjurTlsCaPath", cert);

    final TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
    tmf.init(ks);
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, tmf.getTrustManagers(), null);

    HttpClient client = HttpClient.newBuilder().sslContext(sslContext).build();

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseURL + "/authn-jwt/" + serviceID +"/"+ conjurAccount +"/authenticate"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Accept-Encoding", "base64")
            .POST(HttpRequest.BodyPublishers.ofString("jwt=" + tokenContent))
            .build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    String accessToken = response.body();
    request = HttpRequest.newBuilder()
            .uri(URI.create(baseURL + "/api/secrets/"+ conjurAccount +"/variable/" + secretPath))
            .header("Authorization", "Token token=\"" + accessToken.toString() + "\"")
            .build();
    response = client.send(request, HttpResponse.BodyHandlers.ofString());
    //System.out.println("Password with HttpClient: " + response.body());

    return response.body();
  }
}